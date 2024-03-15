package it.carmelolagamba.saveyourtime.ui.preferences

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import dagger.hilt.android.AndroidEntryPoint
import it.carmelolagamba.saveyourtime.R
import it.carmelolagamba.saveyourtime.databinding.FragmentPreferencesBinding
import it.carmelolagamba.saveyourtime.service.PreferencesService
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * @author carmelolg
 * @since version 1.0
 */
@AndroidEntryPoint
class PreferencesFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentPreferencesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var preferencesService: PreferencesService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreferencesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /**
        binding.stopApplication.setOnCheckedChangeListener { _, isChecked ->
        preferencesService.updateAppBlockPreference(isChecked)
        }

        binding.stopApplication.isChecked = preferencesService.isAppBlockEnabled()
         */

        binding.enableReminder.setOnCheckedChangeListener { _, isChecked ->
            preferencesService.updateAppReminderPreference(isChecked)
            if (binding.reminderTimeChoice.selectedItem != null) {
                preferencesService.updateAppReminderTimePreference(binding.reminderTimeChoice.selectedItem.toString())
            }
            binding.reminderTimeChoice.isEnabled = binding.enableReminder.isChecked
        }

        binding.enableReminder.isChecked = preferencesService.isAppReminderEnabled()

        binding.reminderTimeChoice.isEnabled = binding.enableReminder.isChecked
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.section_notification_app_option_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.reminderTimeChoice.adapter = adapter
        }

        binding.reminderTimeChoice.onItemSelectedListener = this

        val reminderTimeChoiceCurrentIndex =
            requireContext().resources.getStringArray(R.array.section_notification_app_option_list)
                .indexOf(preferencesService.findAppReminderTimePreference().toString())
        binding.reminderTimeChoice.setSelection(reminderTimeChoiceCurrentIndex)


        binding.resetButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(resources.getText(R.string.dialog_reset_title))
            builder.setMessage(resources.getText(R.string.dialog_reset_description))

            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                lifecycleScope.launch {
                    preferencesService.resetAll()
                    resetAll()
                }
            }

            builder.setNegativeButton(android.R.string.cancel) { _, _ ->
            }

            builder.show()
        }

        binding.privacyButton.setOnClickListener {
            openBrowser(resources.getString(R.string.privacy_url))
        }

        binding.licenseButton.setOnClickListener {
            openBrowser(resources.getString(R.string.license_url))
        }

        val balloon = Balloon.Builder(requireContext())
            .setWidthRatio(0.9f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setText(requireContext().resources.getText(R.string.section_notification_app_option_info))
            .setTextColorResource(R.color.white)
            .setTextSize(15f)
            .setTextGravity(Gravity.START)
            .setIconDrawableResource(R.drawable.ic_info)
            .setIconColor(resources.getColor(R.color.white, requireContext().theme))
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowSize(10)
            .setArrowPosition(0.5f)
            .setPadding(12)
            .setCornerRadius(8f)
            .setBackgroundColorResource(R.color.fourth)
            .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
            .setLifecycleOwner(this)
            .build()

        binding.infoIcon.setOnClickListener {
            balloon.showAlignBottom(binding.infoIcon)
        }

        balloon.setOnBalloonClickListener {
            balloon.dismiss()
        }

        return root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        preferencesService.updateAppReminderTimePreference(binding.reminderTimeChoice.selectedItem.toString())
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    private fun openBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun resetAll() {
        binding.stopApplication.isChecked = false
        binding.enableReminder.isChecked = false
        binding.reminderTimeChoice.setSelection(0)
    }
}