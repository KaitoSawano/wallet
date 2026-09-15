/*
 * Copyright 2025 ACINQ SAS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.acinq.zeeygo.android.components.auth.spendinglock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.acinq.zeeygo.android.ZeeyGoApplication
import fr.acinq.zeeygo.android.WalletId
import fr.acinq.zeeygo.android.components.auth.pincode.CheckPinViewModel
import fr.acinq.zeeygo.android.security.PinManager
import fr.acinq.zeeygo.android.utils.datastore.DataStoreManager
import fr.acinq.zeeygo.android.utils.datastore.UserPrefs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CheckSpendingPinViewModel(override val application: ZeeyGoApplication, override val walletId: WalletId) : CheckPinViewModel() {

    val userPrefs: UserPrefs by lazy { DataStoreManager.loadUserPrefsForWallet(context = application.applicationContext, walletId) }

    init {
        viewModelScope.launch { monitorPinCodeAttempts() }
    }

    override suspend fun getPinCodeAttempt(): Flow<Int> {
        return userPrefs.getSpendingPinCodeAttempt
    }

    override suspend fun savePinCodeSuccess() {
        userPrefs.saveSpendingPinCodeSuccess()
    }

    override suspend fun savePinCodeFailure() {
        userPrefs.saveSpendingPinCodeFailure()
    }

    override suspend fun getExpectedPin(): String? {
        return PinManager.getSpendingPinMapFromDisk(application.applicationContext)[walletId]
    }

    override suspend fun resetPinPrefs() {
        userPrefs.saveIsSpendLockPinEnabled(false)
        userPrefs.saveSpendingPinCodeSuccess()
    }

    class Factory(val application: ZeeyGoApplication, val walletId: WalletId) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CheckSpendingPinViewModel(application, walletId) as T
        }
    }
}