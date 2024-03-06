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
import it.carmelolagamba.saveyourtime.databinding.ActivitySecondaryBinding

/**
 * @author carmelolg
 * @since version 1.0
 */
@AndroidEntryPoint
class SecondaryActivity : AbstractActivity() {

    private lateinit var binding: ActivitySecondaryBinding
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivitySecondaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_history)

        navView.findViewById<View>(R.id.navigation_history_dashboard).setOnClickListener {
            val navController = this.findNavController(R.id.nav_host_fragment_activity_history)
            navController.navigate(R.id.navigation_history_dashboard)

        }

        navView.findViewById<View>(R.id.navigation_preferences).setOnClickListener {
            val navController = this.findNavController(R.id.nav_host_fragment_activity_history)
            navController.navigate(R.id.navigation_preferences)

        }

        navView.findViewById<View>(R.id.navigation_to_left).setOnClickListener {
            val main = Intent(this, MainActivity::class.java)
            startActivity(main)
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


