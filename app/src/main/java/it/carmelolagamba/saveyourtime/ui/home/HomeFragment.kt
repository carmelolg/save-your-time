package it.carmelolagamba.saveyourtime.ui.home

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import it.carmelolagamba.saveyourtime.databinding.FragmentHomeBinding
import it.carmelolagamba.saveyourtime.service.AppService
import it.carmelolagamba.saveyourtime.service.UtilService
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    @Inject
    lateinit var utilService: UtilService

    @Inject
    lateinit var appService: AppService


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val statsManager = requireContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        var statsUsageMap = statsManager.queryAndAggregateUsageStats(utilService.todayMidnightMillis(), utilService.tomorrowMidnightMillis())


        statsUsageMap = statsUsageMap.toList()
            .sortedByDescending { (_, value) -> value.totalTimeInForeground }
            //.map { (_, value) -> value.setTotalTimeInForeground() = (value.totalTimeInForeground)/(1000*60) }
            .toMap()

        statsUsageMap = statsUsageMap.toList().filter { (application, _) -> utilService.appListPackageName().contains(application) }.toMap()

        val txt: TextView = binding.textHome
        //txt.text = "Ciao"
        var status: String = ""
        statsUsageMap.forEach { _, value ->  status += " " + appService.findNameByPackageName(value.packageName) + " seconds: " + value.totalTimeInForeground / (1000*60) + " \n"}

        txt.text = status

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}