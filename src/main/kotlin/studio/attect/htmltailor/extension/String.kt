package studio.attect.htmltailor.extension

import studio.attect.htmltailor.HtmlTailor

/**
 * 为String对象扩展一个返回处理了屏蔽词的字符串的方法
 */
fun String?.blockWord():String?{
    if(this.isNullOrBlank()) return null
    var afterBlockText = this?:"" //Kotlin Bug
    HtmlTailor.blockWords.forEach {
        afterBlockText = afterBlockText.replace(it,HtmlTailor.blockWordPatch,true)
    }
    return afterBlockText
}
