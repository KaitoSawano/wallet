package fr.acinq.zeeygo.managers

import fr.acinq.bitcoin.Chain
import fr.acinq.bitcoin.PublicKey
import fr.acinq.bitcoin.byteVector
import fr.acinq.lightning.db.Databases
import fr.acinq.lightning.logging.LoggerFactory
import fr.acinq.lightning.logging.debug
import fr.acinq.zeeygo.ZeeyGoBusiness
import fr.acinq.zeeygo.db.SqliteAppDb
import fr.acinq.zeeygo.db.SqliteChannelsDb
import fr.acinq.zeeygo.db.SqlitePaymentsDb
import fr.acinq.zeeygo.db.contacts.SqliteContactsDb
import fr.acinq.zeeygo.db.createChannelsDbDriver
import fr.acinq.zeeygo.db.createPaymentsDbDriver
import fr.acinq.zeeygo.db.createSqliteChannelsDb
import fr.acinq.zeeygo.db.createSqlitePaymentsDb
import fr.acinq.zeeygo.db.makeCloudKitDb
import fr.acinq.zeeygo.db.payments.CloudKitInterface
import fr.acinq.zeeygo.defaultScope
import fr.acinq.zeeygo.managers.global.CurrencyManager
import fr.acinq.zeeygo.utils.PlatformContext
import fr.acinq.zeeygo.utils.extensions.zeeygoName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

class DatabaseManager(
    loggerFactory: LoggerFactory,
    private val ctx: PlatformContext,
    private val chain: Chain,
    private val appDb: SqliteAppDb,
    private val nodeParamsManager: NodeParamsManager,
    appConfigurationManager: AppConfigurationManager,
    currencyManager: CurrencyManager,
) : CoroutineScope by defaultScope() {

    constructor(business: ZeeyGoBusiness): this(
        loggerFactory = business.loggerFactory,
        ctx = business.zeeygoGlobal.ctx,
        chain = business.chain,
        appDb = business.zeeygoGlobal.appDb,
        nodeParamsManager = business.nodeParamsManager,
        appConfigurationManager = business.appConfigurationManager,
        currencyManager = business.zeeygoGlobal.currencyManager,
    )

    private val log = loggerFactory.newLogger(this::class)

    private val _databases = MutableStateFlow<ZeeyGoDatabases?>(null)
    val databases: StateFlow<ZeeyGoDatabases?> = _databases.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val contactsList = _databases.filterNotNull().flatMapLatest { it.payments.contacts.contactsList }

    @OptIn(ExperimentalCoroutinesApi::class)
    val contactsDb = _databases.filterNotNull().mapLatest { it.payments.contacts }

    val paymentMetadataQueue = PaymentMetadataQueue(currencyManager = currencyManager, appConfigurationManager = appConfigurationManager)

    init {
        launch {
            nodeParamsManager.nodeParams.collect { nodeParams ->
                if (nodeParams == null) return@collect
                log.debug { "nodeParams available: building databases..." }

                val channelsDbDriver = createChannelsDbDriver(ctx, channelsDbName(chain, nodeParams.nodeId))
                val channelsDb = createSqliteChannelsDb(channelsDbDriver)
                val paymentsDbDriver = createPaymentsDbDriver(ctx, paymentsDbName(chain, nodeParams.nodeId)) { log.e { "payments-db migration error: $it" } }
                val paymentsDb = createSqlitePaymentsDb(paymentsDbDriver, paymentMetadataQueue, loggerFactory)
                val cloudKitDb = makeCloudKitDb(appDb, paymentsDb)
                log.debug { "databases object created" }
                _databases.value = ZeeyGoDatabases(
                    channels = channelsDb,
                    payments = paymentsDb,
                    cloudKit = cloudKitDb,
                )
            }
        }
        launch {
            paymentsDb().contacts.migrateContactsIfNeeded(appDb)
        }
    }

    fun close() {
        val db = databases.value
        if (db != null) {
            db.channels.close()
            db.payments.close()
        }
    }

    suspend fun paymentsDb(): SqlitePaymentsDb {
        val db = databases.filterNotNull().first()
        return db.payments
    }

    suspend fun contactsDb(): SqliteContactsDb {
        return paymentsDb().contacts
    }

    suspend fun cloudKitDb(): CloudKitInterface? {
        val db = databases.filterNotNull().first()
        return db.cloudKit
    }

    companion object {
        fun channelsDbName(chain: Chain, nodeId: PublicKey): String {
            return "channels-${chain.zeeygoName.lowercase()}-${nodeId.hash160().byteVector().toHex()}.sqlite"
        }

        fun paymentsDbName(chain: Chain, nodeId: PublicKey): String {
            return "payments-${chain.zeeygoName.lowercase()}-${nodeId.hash160().byteVector().toHex()}.sqlite"
        }
    }
}

data class ZeeyGoDatabases(
    override val channels: SqliteChannelsDb,
    override val payments: SqlitePaymentsDb,
    val cloudKit: CloudKitInterface?,
): Databases
