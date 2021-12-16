package com.hua.kuaichuan.services

import android.content.Context
import android.os.Environment
import com.yanzhenjie.andserver.annotation.Config
import com.yanzhenjie.andserver.framework.config.WebConfig
import com.yanzhenjie.andserver.framework.website.FileBrowser
import com.yanzhenjie.andserver.framework.website.StorageWebsite

/**
 * @author : huaweikai
 * @Date   : 2021/12/15
 * @Desc   : config
 */
@Config
class MyConfig :WebConfig {
    companion object{
        var path = "/sdcard/"
    }
    override fun onConfig(context: Context?, delegate: WebConfig.Delegate?) {
        delegate?.addWebsite(FileBrowser(path))
    }
}
//@Config
//class ShareFile(private val fileWeb:String):WebConfig{
//    override fun onConfig(context: Context?, delegate: WebConfig.Delegate?) {
//        delegate?.addWebsite(StorageWebsite(fileWeb))
//    }
//}