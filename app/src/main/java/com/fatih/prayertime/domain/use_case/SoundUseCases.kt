package com.fatih.prayertime.domain.use_case

import com.fatih.prayertime.domain.use_case.sound.GetSoundsUseCase
import com.fatih.prayertime.domain.use_case.sound.PlaySoundUseCase
import com.fatih.prayertime.domain.use_case.sound.StopSoundUseCase
import javax.inject.Inject

data class SoundUseCases @Inject constructor(
    val getSoundsUseCase: GetSoundsUseCase,
    val playSoundUseCase: PlaySoundUseCase,
    val stopSoundUseCase: StopSoundUseCase
) 