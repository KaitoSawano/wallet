package fr.acinq.zeeygo.utils

import fr.acinq.bitcoin.Chain
import fr.acinq.bitcoin.TxId


class BlockchainExplorer(private val chain: Chain) {

    sealed class Website(val base: String) {
        object MempoolSpace: Website("https://mempool.space")
        object BlockstreamInfo: Website("https://blockstream.info")
    }

    fun txUrl(txId: TxId, website: Website = Website.MempoolSpace): String {
        return when (website) {
            Website.MempoolSpace -> {
                when (chain) {
                    Chain.Mainnet -> "${website.base}/tx/$txId"
                    Chain.Mainnet3 -> "${website.base}/mainnet/tx/$txId"
                    Chain.Mainnet4 -> "${website.base}/mainnet4/tx/$txId"
                    Chain.Signet -> "${website.base}/signet/tx/$txId"
                    Chain.Regtest -> "${website.base}/_REGTEST_/tx/$txId"
                }
            }
            Website.BlockstreamInfo -> {
                when (chain) {
                    Chain.Mainnet -> "${website.base}/tx/$txId"
                    Chain.Mainnet3 -> "${website.base}/mainnet/tx/$txId"
                    Chain.Mainnet4 -> "${website.base}/mainnet4/tx/$txId"
                    Chain.Signet -> "${website.base}/signet/tx/$txId"
                    Chain.Regtest -> "${website.base}/_REGTEST_/tx/$txId"
                }
            }
        }
    }

    fun addressUrl(addr: String, website: Website = Website.MempoolSpace): String {
        return when (website) {
            Website.MempoolSpace -> {
                when (chain) {
                    Chain.Mainnet -> "${website.base}/address/$addr"
                    Chain.Mainnet3 -> "${website.base}/mainnet/address/$addr"
                    Chain.Mainnet4 -> "${website.base}/mainnet4/address/$addr"
                    Chain.Signet -> "${website.base}/signet/address/$addr"
                    Chain.Regtest -> "${website.base}/_REGTEST_/address/$addr"
                }
            }
            Website.BlockstreamInfo -> {
                when (chain) {
                    Chain.Mainnet -> "${website.base}/address/$addr"
                    Chain.Mainnet3 -> "${website.base}/mainnet/address/$addr"
                    Chain.Mainnet4 -> "${website.base}/mainnet4/address/$addr"
                    Chain.Signet -> "${website.base}/signet/address/$addr"
                    Chain.Regtest -> "${website.base}/_REGTEST_/address/$addr"
                }
            }
        }
    }
}