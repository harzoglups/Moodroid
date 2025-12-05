package com.moode.android.domain.usecase

import com.moode.android.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use case for clearing URL history
 */
class ClearUrlHistoryUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke() {
        settingsRepository.clearUrlHistory()
    }
}
