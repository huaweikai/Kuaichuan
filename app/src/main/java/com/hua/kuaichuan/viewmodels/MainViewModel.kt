package com.hua.kuaichuan.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.WorkManager
import com.hua.kuaichuan.others.NetUtils
import com.hua.kuaichuan.others.ServerState
import com.hua.kuaichuan.services.ServerService
import com.hua.kuaichuan.ui.config.TextState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * @author : huaweikai
 * @Date   : 2021/12/15
 * @Desc   : mainViewModel
 */
class MainViewModel():ViewModel() {
    private var _portText = mutableStateOf(
        TextState(hint = "8080")
    )
    val portText:State<TextState>  = _portText


    private val _buttonText = mutableStateOf("点击开启")
    val buttonText :State<String> get() = _buttonText
    @SuppressLint("StaticFieldLeak")
    private lateinit var service:ServerService
    private lateinit var job: Job

    private val _ipaddr = mutableStateOf(NetUtils.localIPAddress)
    val ipaddr :State<String> get() = _ipaddr

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            service?.let {
                this@MainViewModel.service = (it as ServerService.ServiceBinder).service.also {
                    job = viewModelScope.launch {
                        it.serverState.collect {state->
                            when(state){
                                is ServerState.Complete-> _buttonText.value = "点击关闭"
                                is ServerState.Close -> _buttonText.value = "点击开启"
                                is ServerState.ServerError -> _buttonText.value = state.message.toString()
                            }
                        }
                    }
                }
            }
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            job.cancel()
        }
    }

    fun bindServerService(context:Context){
        Intent(context,ServerService::class.java).also {
            context.bindService(it,serviceConnection, ComponentActivity.BIND_AUTO_CREATE)
        }
    }

    fun updateIpAddress(ip:String){
        _ipaddr.value = ip
    }

    fun changeText(text:String){
        _portText.value = _portText.value.copy(
            text =text.filter { it.isDigit() }
        )
    }
    fun changeFocus(state:FocusState){
        _portText.value = _portText.value.copy(
            isHintVisible = !state.isFocused && _portText.value.text.isBlank()
        )
    }

    fun openOrOff(){
        val port = if(portText.value.text.isBlank()){
            portText.value.hint.toInt()
        }else{
            portText.value.text.toInt()
        }
        service.openOrOffServer(port)
    }
}