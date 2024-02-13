package it.carmelolagamba.saveyourtime.ui.info

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!
    private lateinit var adapter: AppDataAdapter

    @Inject
    lateinit var appService: AppService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val progressBar = requireActivity().findViewById<ProgressBar>(R.id.progressbar)

        lifecycleScope.launch {
            val applications = retrieveApps()
            adapter = AppDataAdapter(applications!!, appService, requireContext())

            val gridView: GridView = binding.gridList
            gridView.adapter = adapter

            progressBar.visibility = View.INVISIBLE
        }

        return root
    }

    private suspend fun retrieveApps():  MutableList<AppDataModel> = withContext(Dispatchers.IO) {

        val applications: MutableList<AppDataModel> = mutableListOf()
        val pm: PackageManager = requireContext().packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        for (packageInfo in packages) {

            val app = appService.findByPackageName(packageInfo.packageName)
            val icon = pm.getApplicationIcon(packageInfo.packageName)

            if (app != null) {
                applications += AppDataModel(icon, app.name, app.packageName, app.selected, app.notifyTime, app.todayUsage)
            } else {
                val appInfo: ApplicationInfo = pm.getApplicationInfo(packageInfo.packageName, 0)
                val appName: String = pm.getApplicationLabel(appInfo).toString()
                applications.add(AppDataModel(icon, appName, packageInfo.packageName, false, 60, 0))



                viewLifecycleOwner.lifecycleScope.launch {
                    appService.insert(
                        App(
                            appName,
                            packageInfo.packageName,
                            false,
                            60,
                            0
                        )
                    )
                }
            }
        }

        applications.sortWith(compareBy<AppDataModel> {it.checked}.reversed().thenBy { it.name })
        return@withContext applications
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}