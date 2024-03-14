package it.carmelolagamba.saveyourtime

import android.app.AlarmManager
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import dagger.hilt.android.AndroidEntryPoint
import it.carmelolagamba.saveyourtime.databinding.ActivityStartBinding
import it.carmelolagamba.saveyourtime.service.AppService
import it.carmelolagamba.saveyourtime.service.worker.SYTBackgroundService
import javax.inject.Inject


/**
 * @author carmelolg
 * @since version 1.0
 */
@AndroidEntryPoint
class StartActivity : AbstractActivity() {

    private lateinit var binding: ActivityStartBinding

    @Inject
    lateinit var appService: AppService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SYTBackgroundService.startService(this)

        initPage()
    }

    override fun onResume() {
        super.onResume()

        initPage()
    }

    private fun initPage() {
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), packageName
        )

        val powerManager: PowerManager =
            applicationContext.getSystemService(POWER_SERVICE) as PowerManager

        val ignoreBatteryLifeOptimizationCheck =
            powerManager.isIgnoringBatteryOptimizations(packageName)

        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (mode != AppOpsManager.MODE_ALLOWED) {

            Log.d("SYT Permission", "Usage data not Allowed")

            binding.allowButton.visibility = View.VISIBLE
            binding.disclaimer.visibility = View.VISIBLE
            binding.allowButtonNotification.visibility = View.INVISIBLE
            binding.disclaimerNotification.visibility = View.INVISIBLE
            binding.allowButtonAlarms.visibility = View.INVISIBLE
            binding.disclaimerAlarms.visibility = View.INVISIBLE

            binding.allowButton.setOnClickListener {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                launcher.launch(intent)
            }

        } else if (!isNotificationChannelEnabled(
                this, resources.getString(R.string.notification_channel_id)
            )
        ) {

            Log.d("SYT Permission", "Channel Notification ID not Allowed")

            binding.allowButton.visibility = View.INVISIBLE
            binding.disclaimer.visibility = View.INVISIBLE
            binding.allowButtonAlarms.visibility = View.INVISIBLE
            binding.disclaimerAlarms.visibility = View.INVISIBLE
            binding.allowButtonNotification.visibility = View.VISIBLE
            binding.disclaimerNotification.visibility = View.VISIBLE

            binding.allowButtonNotification.setOnClickListener {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                //.putExtra(Settings.EXTRA_CHANNEL_ID, resources.getString(R.string.notification_channel_id))
                launcher.launch(intent)
            }

        } else if (!ignoreBatteryLifeOptimizationCheck) {
            binding.allowButton.visibility = View.INVISIBLE
            binding.disclaimer.visibility = View.INVISIBLE
            binding.allowButtonNotification.visibility = View.INVISIBLE
            binding.disclaimerNotification.visibility = View.INVISIBLE

            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                .setData(Uri.parse("package:$packageName"))
            launcher.launch(intent)

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            binding.allowButton.visibility = View.INVISIBLE
            binding.disclaimer.visibility = View.INVISIBLE
            binding.allowButtonNotification.visibility = View.INVISIBLE
            binding.disclaimerNotification.visibility = View.INVISIBLE
            binding.allowButtonAlarms.visibility = View.VISIBLE
            binding.disclaimerAlarms.visibility = View.VISIBLE

            binding.allowButtonAlarms.setOnClickListener {
                launcher.launch(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).setData(Uri.parse("package:$packageName")))
            }
        } else if (isAllCheckPassed()) {
            Log.d("SYT Permission", "Allowed")
            startMainActivity()
            finish()
        }
    }

    private val launcher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode === RESULT_OK) {
                Log.d("SYT Permission", "Allowed ok")
            }
        })

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
