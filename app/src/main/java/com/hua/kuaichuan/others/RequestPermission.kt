package com.hua.kuaichuan.others

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

/**
 * @author : huaweikai
 * @Date   : 2021/12/15
 * @Desc   : permission
 */
object RequestPermission {
    fun request(activity: ComponentActivity):Boolean{
        var isGet = true
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions->
            permissions.entries.forEach {
                if(!it.value){
                    isGet = false
                }
            }
        }.launch(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
        )
        return isGet
    }
}