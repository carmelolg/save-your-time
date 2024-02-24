package it.carmelolagamba.saveyourtime.ui.settings

import android.app.AlertDialog
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import it.carmelolagamba.saveyourtime.R
import it.carmelolagamba.saveyourtime.databinding.FragmentSettingsBinding
import it.carmelolagamba.saveyourtime.persistence.App
import it.carmelolagamba.saveyourtime.service.AppService
import it.carmelolagamba.saveyourtime.service.EventService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author carmelolg
 * @since version 1.0
 */
@AndroidEntryPoint
class SettingsFragment : Fragment() /*AbstractFragment()*/ {

    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!
    private lateinit var adapter: AppDataAdapter

    @Inject
    lateinit var appService: AppService

    @Inject
    lateinit var eventService: EventService

    /**
    override fun innerOnScroll(x: Float, y: Float) {
    //binding.gridList.smoothScrollToPosition(y.toInt())
    //binding.gridList.scrollX = x.toInt()
    //binding.gridList.scrollY = y.toInt()
    binding.gridList.scrollListBy(y.toInt())
    binding.gridList.scrollBarFadeDuration = 2
    binding.gridList.scrollBarSize = 10
    }
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this)[SettingsViewModel::class.java]

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /** Start progress bar while rendering is active */
        val progressBar = requireActivity().findViewById<ProgressBar>(R.id.progressbar)
        progressBar.visibility = View.VISIBLE

        val resetButton = binding.resetButton

        /** Dialog if floating button (delete) is clicked */
        resetButton.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            builder.setTitle(resources.getText(R.string.dialog_reset_title))
            builder.setMessage(resources.getText(R.string.dialog_reset_description))

            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                lifecycleScope.launch {
                    resetPage()
                }
            }

            builder.setNegativeButton(android.R.string.cancel) { _, _ ->
                /**Toast.makeText(
                context,
                android.R.string.cancel, Toast.LENGTH_SHORT
                ).show()*/
            }

            builder.show()
        }

        /** Setting behaviour about scrolling on list for fab */
        binding.gridList.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            val x = scrollY - oldScrollY
            if (x > 0) {
                resetButton.visibility = View.VISIBLE
            } else if (x < 0) {
                resetButton.visibility = View.INVISIBLE
            }
        }

        /** Init page, rendering all apps */
        lifecycleScope.launch {
            initPage()
        }

        return root
    }

    private suspend fun initPage() {

        blockButtons()

        val applications = retrieveApps()

        var totalTimeChecked = 0
        applications.filter { it.notifyTime > 0 && it.checked }.map { it.notifyTime }.forEach {
            Log.d("SYT", it.toString())
            totalTimeChecked += it
        }

        if (totalTimeChecked > 0) {
            binding.bottomLayout.visibility = View.VISIBLE
            val hours: Int = totalTimeChecked / 60
            val minutes: Int = totalTimeChecked % 60
            binding.totalTime.text = binding.totalTime.text.toString() + " $hours h, $minutes min"
        } else {
            binding.bottomLayout.visibility = View.INVISIBLE
        }
        
        adapter = AppDataAdapter(applications, appService, eventService, requireContext())

        val gridView: GridView = binding.gridList
        gridView.adapter = adapter

        /**
        gridView.setOnTouchListener { _, event ->
        gestureDetector.onTouchEvent(event)
        }
         */

        requireActivity().findViewById<ProgressBar>(R.id.progressbar).visibility = View.INVISIBLE

        unblockButtons()
    }

    private suspend fun resetPage() {

        requireActivity().findViewById<ProgressBar>(R.id.progressbar).visibility = View.VISIBLE

        /** Reset all data on DB */
        appService.resetAll()
        eventService.resetAll()

        /** Re-init page*/
        initPage()
    }

    private suspend fun retrieveApps(): MutableList<AppDataModel> = withContext(Dispatchers.IO) {

        val applications: MutableList<AppDataModel> = mutableListOf()
        val pm: PackageManager = requireContext().packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        for (packageInfo in packages) {

            if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                val app = appService.findByPackageName(packageInfo.packageName)
                val icon = pm.getApplicationIcon(packageInfo.packageName)

                if (app != null) {
                    applications += AppDataModel(
                        icon,
                        app.name,
                        app.packageName,
                        app.selected,
                        app.notifyTime,
                        app.todayUsage,
                        app.lastUpdate
                    )
                } else {
                    val appInfo: ApplicationInfo = pm.getApplicationInfo(packageInfo.packageName, 0)
                    val appName: String = pm.getApplicationLabel(appInfo).toString()
                    applications.add(
                        AppDataModel(
                            icon,
                            appName,
                            packageInfo.packageName,
                            false,
                            60,
                            0,
                            System.currentTimeMillis()
                        )
                    )



                    viewLifecycleOwner.lifecycleScope.launch {
                        appService.insert(
                            App(
                                appName,
                                packageInfo.packageName,
                                false,
                                60,
                                0,
                                System.currentTimeMillis()
                            )
                        )
                    }
                }
            }
        }

        applications.sortWith(
            compareBy<AppDataModel> { it.checked }.reversed().thenBy { it.name?.lowercase() })
        return@withContext applications
    }

    private fun blockButtons() {
        requireActivity().findViewById<View>(R.id.navigation_home).isEnabled = false
        requireActivity().findViewById<View>(R.id.navigation_info).isEnabled = false
        binding.resetButton.visibility = View.INVISIBLE
        binding.bottomLayout.visibility = View.INVISIBLE
    }

    private fun unblockButtons() {
        requireActivity().findViewById<View>(R.id.navigation_home).isEnabled = true
        requireActivity().findViewById<View>(R.id.navigation_info).isEnabled = true
        binding.bottomLayout.visibility = View.VISIBLE
        binding.resetButton.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}