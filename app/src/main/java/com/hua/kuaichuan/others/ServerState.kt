package com.hua.kuaichuan.others

/**
 * @author : huaweikai
 * @Date   : 2021/12/15
 * @Desc   : 状态
 */
sealed class ServerState {
    object Complete:ServerState()
    object Close:ServerState()
    data class ServerError(val message:String?) :ServerState()
}