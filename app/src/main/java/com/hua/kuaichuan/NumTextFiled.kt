package com.hua.kuaichuan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

/**
 * @author : huaweikai
 * @Date   : 2021/12/15
 * @Desc   : 数字输入键盘
 */
@Composable
fun NumTextFiled(
    text:String,
    hint:String,
    onTextChange:(String)->Unit,
    isHintVisible:Boolean = true,
    singleLine:Boolean = false,
    onFocusChange:(FocusState)->Unit
) {
    Box(
        contentAlignment = Alignment.CenterStart
    ) {
        TextField(
            value = text,
            onValueChange = onTextChange,
            singleLine = singleLine,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    onFocusChange(it)
                }
        )
        if(isHintVisible){
            Text(text = hint, color = Color.Gray)
        }
    }

}