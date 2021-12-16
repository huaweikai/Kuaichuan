package com.hua.kuaichuan

import android.app.Activity
import android.content.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hua.kuaichuan.others.NetUtils
import com.hua.kuaichuan.others.RequestPermission
import com.hua.kuaichuan.services.MyConfig
import com.hua.kuaichuan.ui.theme.KuaichuanTheme
import com.hua.kuaichuan.viewmodels.MainViewModel
import com.yanzhenjie.andserver.framework.config.Multipart
import com.yanzhenjie.andserver.framework.config.WebConfig
import com.yanzhenjie.andserver.framework.website.Website


private const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private val  wifiApBroadcast = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent:Intent?) {
            val action = intent?.action;
            if("android.net.wifi.WIFI_AP_STATE_CHANGED" == action){
                viewModel.updateIpAddress(NetUtils.localIPAddress)
            }
        }
    }

    private val intentFilter = IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KuaichuanTheme {
                SetAnButton()
            }
        }

        registerReceiver(wifiApBroadcast,intentFilter)
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M){
            if(RequestPermission.request(this)) {
                viewModel.bindServerService(this)
            }
        }else{
            viewModel.bindServerService(this)
        }

    }
    @Composable
    fun SetAnButton(){
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val port = viewModel.portText.value
            NumTextFiled(
                text = port.text,
                hint = port.hint,
                onTextChange = { viewModel.changeText(it)},
                onFocusChange = { viewModel.changeFocus(it)},
                isHintVisible = port.isHintVisible,
            )
            Text(
                "当前的ip地址:${viewModel.ipaddr.value}"
            )
            Button(
                onClick = { viewModel.openOrOff() },
            ) {
                Text(text = viewModel.buttonText.value)
            }
            Button(onClick = {
                selectFiles.launch("*/*")
            }) {
                Text(text = "选择文件")
            }
        }
    }
    private val selectFiles = registerForActivityResult(
        OpenMyDocuments()
    ){
        MyConfig.path = it
    }
}
class OpenMyDocuments :ActivityResultContract<String,String>(){
    override fun createIntent(context: Context, input: String): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String {
        val data = intent?.takeIf {
            resultCode == Activity.RESULT_OK
        }?.data?.path
        val index = data?.indexOf(":")
        val lastPath = if (index != null) {
            data.substring(index + 1)
        } else {
            null
        }
        return "/sdcard/$lastPath"
    }
}



