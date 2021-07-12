package com.example.covpasssdkdemotest

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.covpasssdkdemotest.databinding.ActivityMainBinding
import de.rki.covpass.sdk.cert.toTrustedCerts
import de.rki.covpass.sdk.dependencies.SdkDependencies
import de.rki.covpass.sdk.dependencies.sdkDeps
import de.rki.covpass.sdk.utils.DSC_UPDATE_INTERVAL_HOURS
import de.rki.covpass.sdk.utils.DscListUpdater
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sdkDeps = object : SdkDependencies() {
            override val application: Application = getApplication()
        }

        sdkDeps.validator.updateTrustedCerts(sdkDeps.dscRepository.dscList.value.toTrustedCerts())

        val tag = "dscListWorker"
        val dscListWorker: PeriodicWorkRequest =
            PeriodicWorkRequest.Builder(DscListUpdater::class.java, DSC_UPDATE_INTERVAL_HOURS, TimeUnit.HOURS)
                .addTag(tag)
                .build()
        WorkManager.getInstance(application).enqueueUniquePeriodicWork(
            tag,
            ExistingPeriodicWorkPolicy.KEEP,
            dscListWorker,
        )
        binding.textView.text = sdkDeps.trustServiceHost
    }
}