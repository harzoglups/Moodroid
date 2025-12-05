package com.moode.android.domain.usecase

import com.moode.android.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use case for adding URL to history
 */
class AddUrlToHistoryUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(url: String) {
        settingsRepository.addUrlToHistory(url)
    }
}
