package com.moode.android.domain.usecase

import com.moode.android.domain.repository.MoodeRepository
import javax.inject.Inject

/**
 * Use case for resolving mDNS URLs (.local) to IP addresses
 */
class ResolveUrlUseCase @Inject constructor(
    private val moodeRepository: MoodeRepository
) {
    suspend operator fun invoke(url: String): String {
        return moodeRepository.resolveUrl(url)
    }
}
