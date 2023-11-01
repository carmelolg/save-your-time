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
import kotlinx.coroutines.launch
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

        val view = inflater.inflate(R.layout.activity_main, container, false)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressbar) //_binding!!.progressbar

        progressBar.visibility = View.VISIBLE

        val applications: MutableList<AppDataModel> = mutableListOf()
        val pm: PackageManager = requireContext().packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        for (packageInfo in packages) {

            val app = appService.findByPackageName(packageInfo.packageName)
            val icon = pm.getApplicationIcon(packageInfo.packageName)

            if (app != null) {
                applications += AppDataModel(icon, app.name, app.packageName, app.selected)
            } else {
                val appInfo: ApplicationInfo = pm.getApplicationInfo(packageInfo.packageName, 0)
                val appName: String = pm.getApplicationLabel(appInfo).toString()
                applications.add(AppDataModel(icon, appName, packageInfo.packageName, false))
                viewLifecycleOwner.lifecycleScope.launch {
                    appService.insert(
                        App(
                            appName,
                            packageInfo.packageName,
                            false
                        )
                    )
                }
            }
        }

        applications.sortWith(compareBy<AppDataModel> {it.checked}.reversed().thenBy { it.name })
        adapter = AppDataAdapter(applications!!, appService, requireContext())

        val gridView: GridView = binding.gridList
        gridView.adapter = adapter


        progressBar.visibility = View.GONE

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}