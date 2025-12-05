package com.moode.android.domain.usecase

import com.moode.android.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use case for updating the URL
 */
class UpdateUrlUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(url: String) {
        settingsRepository.setUrl(url)
    }
}
