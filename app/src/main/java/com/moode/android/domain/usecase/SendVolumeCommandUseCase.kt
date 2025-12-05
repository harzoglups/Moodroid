package com.moode.android.domain.usecase

import com.moode.android.domain.model.Result
import com.moode.android.domain.repository.MoodeRepository
import javax.inject.Inject

/**
 * Use case for sending volume commands to Moode server
 */
class SendVolumeCommandUseCase @Inject constructor(
    private val moodeRepository: MoodeRepository
) {
    suspend operator fun invoke(url: String, command: String, step: Int): Result<Unit> {
        return moodeRepository.sendVolumeCommand(url, command, step)
    }
}
