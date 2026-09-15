package fr.acinq.zeeygo.controllers.main

import fr.acinq.lightning.MilliSatoshi
import fr.acinq.zeeygo.controllers.MVI

object Home {

    data class Model(
        val balance: MilliSatoshi?,
    ) : MVI.Model()

    val emptyModel = Model(
        balance = null,
    )

    sealed class Intent : MVI.Intent()
}
