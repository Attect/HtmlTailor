package studio.attect.htmltailor

import org.junit.jupiter.api.Test


internal class HtmlTailorTest {

    @Test
    fun newTask() {

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
}