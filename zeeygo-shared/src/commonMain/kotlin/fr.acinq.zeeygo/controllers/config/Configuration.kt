package fr.acinq.zeeygo.controllers.config

import fr.acinq.zeeygo.controllers.MVI

object Configuration {

    sealed class Model : MVI.Model() {
        object SimpleMode : Model()
        object FullMode : Model()
    }

    sealed class Intent : MVI.Intent()
}
