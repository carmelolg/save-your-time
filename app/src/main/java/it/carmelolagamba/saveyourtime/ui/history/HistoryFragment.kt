package it.carmelolagamba.saveyourtime.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import it.carmelolagamba.saveyourtime.databinding.FragmentHistoryDashboardBinding
import it.carmelolagamba.saveyourtime.persistence.App
import it.carmelolagamba.saveyourtime.service.AppService
import javax.inject.Inject


/**
 * @author carmelolg
 * @since version 1.0
 */
@AndroidEntryPoint
class HistoryFragment : Fragment() /*AbstractFragment()*/, AdapterView.OnItemSelectedListener {

    private var _binding: FragmentHistoryDashboardBinding? = null

    @Inject
    lateinit var appService: AppService

    private var apps: List<App> = mutableListOf()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = _binding ?: FragmentHistoryDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        retrieveApps()

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item, apps.map { it.name }
        )

        binding.appHistoryChoice.adapter = adapter

        binding.appHistoryChoice.onItemSelectedListener = this

        return root
    }

    override fun onStart() {
        super.onStart()
        retrieveApps()
        refreshUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun refreshUI() {


    }

    private fun retrieveApps() {

        apps = appService.findAllChecked()

        apps = apps.sortedWith(compareBy<App> { it.todayUsage }.reversed()
            .thenBy { it.name.lowercase() })
            .toList()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //TODO("Not yet implemented")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}