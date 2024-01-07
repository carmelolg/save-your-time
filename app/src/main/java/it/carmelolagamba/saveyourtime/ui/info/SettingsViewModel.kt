package it.carmelolagamba.saveyourtime.ui.info

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.carmelolagamba.saveyourtime.service.AppService
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private var appService: AppService) :
    ViewModel() {

}