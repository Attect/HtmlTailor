package studio.attect.htmltailor

import studio.attect.htmltailor.annotations.HtmlTailorMark

data class OneData(var id:Int,@HtmlTailorMark var name:String) {
    var age = 12
}
