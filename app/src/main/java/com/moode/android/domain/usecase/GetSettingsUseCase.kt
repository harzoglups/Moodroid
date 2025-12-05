package com.moode.android.domain.usecase

import com.moode.android.domain.model.Settings
import com.moode.android.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting user settings
 */
class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<Settings> = settingsRepository.getSettings()
}
