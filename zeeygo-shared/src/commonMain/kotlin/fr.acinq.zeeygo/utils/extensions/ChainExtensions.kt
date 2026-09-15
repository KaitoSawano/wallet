package fr.acinq.zeeygo.utils.extensions

import fr.acinq.bitcoin.Chain

/**
 * Value used by ZeeyGo for naming files relative to the [Chain].
 * Specifically, mainnet3 name must be "mainnet", for historical reasons.
 */
val Chain.zeeygoName: String
    get() = when (this) {
        Chain.Regtest -> "regtest"
        Chain.Signet -> "signet"
        Chain.Mainnet3 -> "mainnet"
        Chain.Mainnet4 -> "mainnet4"
        Chain.Mainnet -> "mainnet"
    }
