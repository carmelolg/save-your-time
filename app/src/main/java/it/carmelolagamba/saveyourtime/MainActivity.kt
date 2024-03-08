package it.carmelolagamba.saveyourtime

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import it.carmelolagamba.saveyourtime.databinding.ActivityMainBinding

/**
 * @author carmelolg
 * @since version 1.0
 */
@AndroidEntryPoint
class MainActivity : AbstractActivity() {

    private lateinit var binding: ActivityMainBinding
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        /** Appbar not used
        val appBarConfiguration = AppBarConfiguration(
        setOf(
        R.id.navigation_home, R.id.navigation_info
        )
        )
         */

        navView.findViewById<View>(R.id.navigation_home).setOnClickListener {
            val navController = this.findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.navigation_home)

        }

        navView.findViewById<View>(R.id.navigation_info).setOnClickListener {
            val navController = this.findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.navigation_info)

        }

        navView.findViewById<View>(R.id.navigation_to_right).setOnClickListener {
            val history = Intent(this, SecondaryActivity::class.java)
            startActivity(history)
            finish()
        }


        /** Appbar not used setupActionBarWithNavController(navController, appBarConfiguration) */
        navView.setupWithNavController(navController)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + 3000 > System.currentTimeMillis()) {
                    finishAndRemoveTask()
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.close_app_message),
                        Toast.LENGTH_LONG
                    ).show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        })

    }

    override fun onResume() {
        super.onResume()

        if (!isAllCheckPassed()) {
            Log.d("SYT Permission", "Not allowed")
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }

    }

}


