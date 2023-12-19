package it.carmelolagamba.saveyourtime

import android.app.AppOpsManager
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import it.carmelolagamba.saveyourtime.databinding.ActivityStartBinding
import it.carmelolagamba.saveyourtime.service.AppService
import javax.inject.Inject


@AndroidEntryPoint
class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding

    @Inject
    lateinit var appService: AppService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), packageName
        )

        if (mode != AppOpsManager.MODE_ALLOWED) {

            Log.d("Permission", "Not Allowed")

            val allowButton = findViewById<Button>(R.id.allow_button)
            allowButton.setOnClickListener {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                launcher.launch(intent)
            }

        } else {
            Log.d("Permission", "Allowed")
            /*
            val applications: MutableList<AppDataModel> = mutableListOf()
            val pm: PackageManager = this.packageManager
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

            for (packageInfo in packages) {
                val appInfo: ApplicationInfo = pm.getApplicationInfo(packageInfo.packageName, 0)
                val appName: String = pm.getApplicationLabel(appInfo).toString()
                applications.add(AppDataModel(null, appName, packageInfo.packageName, false, 0))
                appService.insert(
                    App(
                        Random.nextLong(),
                        appName,
                        packageInfo.packageName,
                        false,
                        0
                    )
                )
            }
            */


            startMainActivity()
        }


    }

    override fun onResume() {
        super.onResume()
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), packageName
        )

        if (mode == AppOpsManager.MODE_ALLOWED) {
            Log.d("Permission", "Allowed")
            startMainActivity()
        }

    }

    private val launcher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.getResultCode() === RESULT_OK) {
                Log.d("Permission", "Allowed ok")
            }
        })

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish();
    }

}
