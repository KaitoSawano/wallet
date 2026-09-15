package fr.acinq.zeeygo.db

import fr.acinq.lightning.utils.UUID
import fr.acinq.zeeygo.db.payments.CloudKitInterface
import fr.acinq.zeeygo.db.sqldelight.AppDatabase
import fr.acinq.zeeygo.db.sqldelight.PaymentsDatabase

actual fun didSaveWalletPayment(id: UUID, database: PaymentsDatabase) {}
actual fun didDeleteWalletPayment(id: UUID, database: PaymentsDatabase) {}
actual fun didUpdateWalletPaymentMetadata(id: UUID, database: PaymentsDatabase) {}

actual fun didSaveContact(contactId: UUID, database: PaymentsDatabase) {}
actual fun didDeleteContact(contactId: UUID, database: PaymentsDatabase) {}

actual fun makeCloudKitDb(appDb: SqliteAppDb, paymentsDb: SqlitePaymentsDb): CloudKitInterface? {
    return null
}