package fr.acinq.zeeygo.db

import fr.acinq.zeeygo.db.payments.*
import kotlinx.coroutines.*

class CloudKitDb(
    appDb: SqliteAppDb,
    paymentsDb: SqlitePaymentsDb
): CloudKitInterface, CoroutineScope by MainScope() {

    val contacts = CloudKitContactsDb(paymentsDb)
    val payments = CloudKitPaymentsDb(paymentsDb)
}
