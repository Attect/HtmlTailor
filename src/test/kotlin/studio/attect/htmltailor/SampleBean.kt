package studio.attect.htmltailor

import studio.attect.htmltailor.annotations.HtmlTailorMark


const val sample = "<html><body>abcd</body><A href=\"http://www.baidu.com\" onclick=\"onAClick()\" style=\"display:flex;float:left;color:red;overflow:x\">链<接</A> <link rel=\"next\" href=\"https://www.php.net/manual/zh/function.implode.php\"><script>\n" +
        "  window.brokenIE = true;\n" +
        " </sCript> <br>first line :AaBc</br> second line &lt;a href=&quot;www.baidu.com&quot; &gt;&lt;/a&gt;<div onclick='hehe*()'>div line</div>}]};(confirm)()//\\\n" +
        "<img src=\"www.someimage.com/image.jpg\"><a href=\"https://muka.app/\">Muka</a>" +
        "<img src='//attect.studio/123' class='redbg large left' onerror='alert(\"hey!\")'>"+
        "<A%0aONMouseOvER%0d=%0d[8].find(confirm)>z\n" +
        "</tiTlE/><a%0donpOintErentER%0d=%0d(prompt)``>z\n" +
        "</SCRiPT/><DETAILs/+/onpoINTERenTEr%0a=%0aa=prompt,a()//" +
        "<script>eval(\\u0061\\u006c\\u0065\\u0072\\u0074(1))</script>"
class SampleBean {
    var id = 0
    var username = "abcdname"
    @HtmlTailorMark
    var introduce:String?=null

    @HtmlTailorMark(TailorLevel.TEXT)
    var testArray = arrayOf(sample)
    @HtmlTailorMark(TailorLevel.NONE)
    var testList = arrayListOf(sample)
    @HtmlTailorMark
    var testMap = mutableMapOf(Pair("mKey",sample))

    override fun toString(): String {
        return "SampleBean(\n\nid=$id,\n\nusername='$username',\n\nintroduce=$introduce,\n\ntestArray=${testArray.contentToString()},\n\ntestList=$testList,\n\ntestMap=$testMap\n\n)"
    }
}

class SampleBeanParent{
    @HtmlTailorMark()
    val testData = SampleBean().apply { introduce = sample }

    override fun toString(): String {
        return "SampleBeanParent(\ntestData=$testData)"
    }

}