package com.fatih.prayertime.domain.use_case.settings_use_cases

import android.content.Context
import android.widget.Toast
import androidx.work.WorkManager
import com.fatih.prayertime.domain.repository.SharedPrefRepository
import javax.inject.Inject

class ClearStatisticSharedPrefUseCase @Inject constructor(
    private val sharedPrefRepository: SharedPrefRepository
) {
    operator fun invoke() {
        sharedPrefRepository.clearStatisticKey()
    }
    
    fun executeWithWorkReset(context: Context) {
        // Önce shared preferences'i temizle
        sharedPrefRepository.clearStatisticKey()
        
        // Sonra çalışan WorkManager işlerini iptal et
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork("StatisticsAlarmWorker")
        
        // Kullanıcıya geri bildirim ver
        Toast.makeText(context, 
            "İstatistik alarmları sıfırlandı. Uygulamayı yeniden başlatmanız gerekebilir.", 
            Toast.LENGTH_LONG).show()
    }
} 