package fr.acinq.zeeygo.controllers.config

import co.touchlab.kermit.Logger
import fr.acinq.lightning.logging.LoggerFactory
import fr.acinq.zeeygo.ZeeyGoBusiness
import fr.acinq.zeeygo.managers.WalletManager
import fr.acinq.zeeygo.controllers.AppController
import kotlinx.coroutines.launch


class AppConfigurationController(
    loggerFactory: LoggerFactory,
    private val walletManager: WalletManager
) : AppController<Configuration.Model, Configuration.Intent>(
    loggerFactory = loggerFactory,
    firstModel = Configuration.Model.SimpleMode
) {
    constructor(business: ZeeyGoBusiness): this(
        loggerFactory = business.loggerFactory,
        walletManager = business.walletManager
    )

    init {
        launch {
            model(
                if (!walletManager.isLoaded())
                    Configuration.Model.SimpleMode
                else
                    Configuration.Model.FullMode
            )
        }
    }

    override fun process(intent: Configuration.Intent) = error("Nothing to process")
}
