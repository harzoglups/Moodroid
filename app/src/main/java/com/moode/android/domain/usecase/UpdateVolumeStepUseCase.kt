package com.moode.android.domain.usecase

import com.moode.android.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use case for updating the volume step
 */
class UpdateVolumeStepUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(volumeStep: Int) {
        settingsRepository.setVolumeStep(volumeStep)
    }
}
