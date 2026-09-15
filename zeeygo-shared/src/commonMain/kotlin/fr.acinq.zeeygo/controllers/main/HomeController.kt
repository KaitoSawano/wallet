package fr.acinq.zeeygo.controllers.main

import fr.acinq.lightning.logging.LoggerFactory
import fr.acinq.zeeygo.ZeeyGoBusiness
import fr.acinq.zeeygo.controllers.AppController
import fr.acinq.zeeygo.managers.BalanceManager
import kotlinx.coroutines.launch


class AppHomeController(
    loggerFactory: LoggerFactory,
    private val balanceManager: BalanceManager
) : AppController<Home.Model, Home.Intent>(
    loggerFactory = loggerFactory,
    firstModel = Home.emptyModel
) {
    constructor(business: ZeeyGoBusiness): this(
        loggerFactory = business.loggerFactory,
        balanceManager = business.balanceManager
    )

    init {
        launch {
            balanceManager.balance.collect {
                model { copy(balance = it) }
            }
        }
    }

    override fun process(intent: Home.Intent) {}
}
