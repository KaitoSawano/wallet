package fr.acinq.zeeygo.controllers.main

import co.touchlab.kermit.Logger
import fr.acinq.lightning.logging.LoggerFactory
import fr.acinq.zeeygo.ZeeyGoBusiness
import fr.acinq.zeeygo.controllers.AppController
import fr.acinq.zeeygo.managers.WalletManager
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class AppContentController(
    loggerFactory: LoggerFactory,
    private val walletManager: WalletManager
) : AppController<Content.Model, Content.Intent>(
    loggerFactory = loggerFactory,
    firstModel = Content.Model.Waiting
) {
    constructor(business: ZeeyGoBusiness): this(
        loggerFactory = business.loggerFactory,
        walletManager = business.walletManager
    )

    init {
        launch {
            if (walletManager.isLoaded()) {
                model(Content.Model.IsInitialized)
            } else {
                model(Content.Model.NeedInitialization)
                // Suspends until a wallet is created
                walletManager.keyManager.filterNotNull().first()
                model(Content.Model.IsInitialized)
            }
        }
    }

    override fun process(intent: Content.Intent) = error("Nothing to process")
}
