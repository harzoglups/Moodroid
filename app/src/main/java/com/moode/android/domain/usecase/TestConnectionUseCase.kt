package com.moode.android.domain.usecase

import com.moode.android.domain.model.Result
import com.moode.android.domain.repository.MoodeRepository
import javax.inject.Inject

/**
 * Use case for testing connection to Moode server
 */
class TestConnectionUseCase @Inject constructor(
    private val moodeRepository: MoodeRepository
) {
    suspend operator fun invoke(url: String): Result<Unit> {
        return moodeRepository.testConnection(url)
    }
}
