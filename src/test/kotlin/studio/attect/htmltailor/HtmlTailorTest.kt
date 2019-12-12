package studio.attect.htmltailor

import org.junit.jupiter.api.Test
import java.io.File


internal class HtmlTailorTest {

    @Test
    fun newTask() {
        val htmlContent = File("src/test/resources/sample.html").readText()
        val tailor = HtmlTailor.defaultTailor.newTask(htmlContent)
        val dir = File("src/test/resources/out")
        if(!dir.isDirectory) dir.delete()
        if(!dir.exists()){
            dir.mkdir()
        }
        File(dir.absolutePath+"/safeString.html").writeText(tailor.safeString?:"")
        File(dir.absolutePath+"/textWithBreakLine.html").writeText(tailor.textWithBreakLine?:"")
        File(dir.absolutePath+"/textString.html").writeText(tailor.textString?:"")
    }

    @Test
    fun doIt() {
        val sampleBean = SampleBean().apply { introduce = sample }
        HtmlTailor.blockWords.add("abc")
        val tailor = HtmlTailor().apply {
            addDraft(
                DesignDraft(
                    "a",
                    allowAttributes = arrayOf("style", "href"),
                    allowStyle = arrayOf("display", "color"),
                    allowUrl = arrayOf("https://muka.app/")
                )
            )
            addDraft(DesignDraft("br"))
            addDraft(
                DesignDraft(
                    "img",
                    DesignDraft.OPERATION_KEEP,
                    arrayOf("src","class"),
                    arrayOf("large"),
                    null,
                    arrayOf("//attect.studio")
                )
            )
            addDraft(DesignDraft("div", DesignDraft.OPERATION_REPLACE, replaceTag = "p"))
        }

        tailor.doIt(sampleBean)
        println(sampleBean)
    }

    @Test
    fun dataClassTest(){
        val myData = OneData(123, sample)
        println()
        println("dataClassTest")
        HtmlTailor.defaultTailor.doIt(myData)
        println(myData.toString())
        println("===================")
    }
}