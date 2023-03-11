package com.isaquesoft.despesas.presentation.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EstadoAppViewModel : ViewModel() {

    val componentes: LiveData<ComponentesVisuais> get() = _componentes

    private val _componentes: MutableLiveData<ComponentesVisuais> =
        MutableLiveData<ComponentesVisuais>().also {
            it.value = temComponentes
        }
    var temComponentes: ComponentesVisuais = ComponentesVisuais()
        set(value) {
            field = value
            _componentes.value = value
        }
}

class ComponentesVisuais(
    val appBar: Boolean = false,
    val temBotaoBack: Boolean = false,
    val temBannerAds: Boolean = false,
)
