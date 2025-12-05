package com.moode.android.domain.usecase

import com.moode.android.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for managing URL history
 */
class GetUrlHistoryUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<List<String>> = settingsRepository.getUrlHistory()
}
