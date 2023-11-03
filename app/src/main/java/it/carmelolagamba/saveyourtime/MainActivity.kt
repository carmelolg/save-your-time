package it.carmelolagamba.saveyourtime

import android.app.AppOpsManager
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import it.carmelolagamba.saveyourtime.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var backPressedTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_info
            )
        )

        navView.findViewById<View>(R.id.navigation_home).setOnClickListener {
            Log.i("", "Home clicked")
            val navController = this.findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.navigation_home)

        }

        navView.findViewById<View>(R.id.navigation_info).setOnClickListener {
            Log.i("", "Info clicked")
            binding.progressbar.visibility = View.VISIBLE
            val navController = this.findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.navigation_info)

        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + 3000 > System.currentTimeMillis()) {
                    finishAndRemoveTask()
                } else {
                    Toast.makeText(applicationContext, getString(R.string.close_app_message), Toast.LENGTH_LONG).show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        })


    }


    override fun onResume() {
        super.onResume()
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), packageName
        )

        if (mode != AppOpsManager.MODE_ALLOWED) {
            Log.d("Permission", "Not allowed")
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }

    }

    /**
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp()
    }
*/

}


