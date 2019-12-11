package studio.attect.htmltailor

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import studio.attect.htmltailor.annotations.HtmlTailorMark
import studio.attect.htmltailor.extension.blockWord
import java.lang.Exception
import java.lang.reflect.Modifier

/**
 * Html裁缝
 * 对用户提交的包含Html内容的字符串进行裁剪修补
 *
 * @author Attect
 */
class HtmlTailor {
    /**
     * Html5标签安全配置
     */
    private val drafts = arrayListOf<DesignDraft>()

    /**
     * 检查结果状态位：是否有危险Html Dom Tag
     */
    private var hasDangerHtmlTag: Boolean? = null

    /**
     * 检查结果状态位：是否有Dom中包含危险属性
     */
    private var hasDangerHtmlAttribute: Boolean? = null

    /**
     * 检查结果状态位：Dom的Style中是否包含危险CSS属性
     */
    private var hasDangerHtmlStyle: Boolean? = null

    /**
     * Jsoup处理字符串为文档
     */
    private var document: Document? = null

    /**
     * 原始字符串
     */
    var originalString: String? = null

    /**
     * 处理之后的安全字符串
     */
    var safeString: String? = null
        get() {
            if (field == null) {
                field = coverStringToSafe()
            }
            return field
        }

    /**
     * 仅包含文本及换行的字符串
     */
    var textWithBreakLine: String? = null
        get() {
            if (field == null) {
                field = coverStringKeepBreakLine()
            }
            return field
        }

    /**
     * 仅包含文本的字符串
     */
    var textString: String? = null
        get() {
            if (field == null) {
                field = document?.text()
                    ?.replace("\n", "")
                    ?.replace("\r", "")
                    ?.replace("&", "&amp;") //这个符号要在前
                    ?.replace("\"", "&quot;")
                    ?.replace("'", "&apos;")
                    ?.replace("<", "&lt;")
                    ?.replace(">", "&gt;")
            }
            return field
        }


    init {
        //默认Jsoup结构
        drafts.add(DesignDraft("#root"))
        drafts.add(DesignDraft("html"))
        drafts.add(DesignDraft("body"))
    }

    /**
     * 新的处理任务
     */
    fun newTask(content: String): HtmlTailor {
        document = Jsoup.parse(content)
        hasDangerHtmlTag = false
        hasDangerHtmlAttribute = false
        hasDangerHtmlStyle = false
        originalString = content
        safeString = null
        textWithBreakLine = null
        textString = null
        return this
    }

    /**
     * 添加一个裁缝设计图纸
     */
    fun addDraft(draft: DesignDraft) {
        if(!drafts.contains(draft)) drafts.add(draft)
    }

    /**
     * 添加多个裁缝设计图纸
     */
    fun addDrafts(drafts:List<DesignDraft>){
        drafts.forEach { addDraft(it) }
    }

    fun addDraft(tagName:String,
                 operation:Int = DesignDraft.OPERATION_KEEP,
                 allowAttributes:Array<String>? = null,
                 allowClass:Array<String>? = null,
                 allowStyle:Array<String>? = null,
                 allowUrl:Array<String>? = null,
                 replaceTag:String? = null){
        drafts.add(DesignDraft(tagName, operation, allowAttributes, allowClass, allowStyle, allowUrl, replaceTag))
    }

    /**
     * 根据给定的裁缝设计图修补内容
     */
    private fun coverStringToSafe(): String {
        return handleWithConfigs()
    }

    /**
     * 按照预订的裁缝设计图制作出只包含换行的文本内容
     */
    private fun coverStringKeepBreakLine(): String {
        val myConfig = arrayListOf(
            DesignDraft("#root"),
            DesignDraft("html"),
            DesignDraft("body"),
            DesignDraft("br"),
            DesignDraft("p"),
            DesignDraft("div", DesignDraft.OPERATION_REPLACE, replaceTag = "p")
        )

        return handleWithConfigs(myConfig)
    }

    private fun handleWithConfigs(config: ArrayList<DesignDraft> = drafts): String {
        document?.allElements?.forEach { element ->
            var isHandled = false
            config.forEach { designDraft ->
                designDraft.handleElement(element).let {
                    if (!isHandled && it) isHandled = true
                }
                if (designDraft.hasRemoveTag) hasDangerHtmlTag = true
                if (designDraft.hasRemoveAttribute) hasDangerHtmlAttribute = true
                if (designDraft.hasRemoveStyle) hasDangerHtmlStyle = true
            }
            if (!isHandled) element.remove()
        }

        return document?.body()?.html() ?: ""
    }

    private fun doTaskWithLevel(content: String?, level: TailorLevel, blockWord: Boolean = true): String? {
        if (content.isNullOrEmpty()) return null
        if (level != TailorLevel.NONE) newTask(content)
        when (level) {
            TailorLevel.NONE -> {
                return if (blockWord) {
                    content.blockWord()
                } else {
                    content
                }
            }
            TailorLevel.SAFE -> {
                return if (blockWord) {
                    safeString.blockWord()
                } else {
                    safeString
                }
            }
            TailorLevel.TEXT_WITH_BREAK_LINE -> {
                return if (blockWord) {
                    textWithBreakLine.blockWord()
                } else {
                    textWithBreakLine
                }
            }
            TailorLevel.TEXT -> {
                return if (blockWord) {
                    textString.blockWord()
                } else {
                    textString
                }
            }
        }
    }

    /**
     * 直接处理一个实例对象[any]中的内容
     * 将会通过反射，自动根据对象类型的class定义，根据HtmlTailorMark注解进行自动操作
     * 效率并不是非常高：笑
     * 不要直接用这个方法处理字符串内容！
     * 根据[level]的值进行不同程度的处理，同时根据[blockWord]决定是否同时处理屏蔽词
     */
    @JvmOverloads
    fun doIt(any: Any?, level: TailorLevel = TailorLevel.NONE, blockWord: Boolean = false) {
        if (any == null || any::class.java.isPrimitive) return
        if (any is Array<*>) {
            if (any.size > 0 && any[0] is String) {
                (any as? Array<String>)?.let { mArray ->
                    for (i in mArray.indices) {
                        doTaskWithLevel(mArray[i], level, blockWord)?.let {
                            mArray[i] = it
                        }
                    }
                }
            } else {
                any.forEach {
                    doIt(it, level, blockWord)
                }
            }
            return
        }
        (any as? List<Any?>)?.let { mList ->
            mList.forEachIndexed { index, value ->
                if (value is String) {
                    (mList as? MutableList)?.let { mutableList ->
                        doTaskWithLevel(value, level, blockWord)?.let {
                            try {
                                mutableList[index] = it
                            } catch (ignore: Exception) {
                            }

                        }
                    }
                } else {
                    doIt(value, level, blockWord)
                }
            }
            return
        }

        (any as? Map<Any?, Any?>)?.let { mMap ->
            mMap.forEach { (key, value) ->
                if (value is String) {
                    (mMap as? MutableMap)?.let { mutableMap ->
                        doTaskWithLevel(value, level, blockWord)?.let {
                            try {
                                mutableMap[key] = it
                            } catch (ignore: Exception) {
                            }
                        }

                    }
                } else {
                    doIt(value, level, blockWord)
                }
            }
            return
        }


        any::class.java.declaredFields.forEach { field ->
            val fieldAnnotation = field.getAnnotation(HtmlTailorMark::class.java)
            if (!Modifier.isTransient(field.modifiers)) {
                val accessible = field.isAccessible
                if (!accessible) field.isAccessible = true
                when (field.type) {
                    String::class.java -> {
                        (field.get(any) as String?)?.let { mString ->
                            if (fieldAnnotation != null) {
                                newTask(mString)
                                when (fieldAnnotation.value) {
                                    TailorLevel.NONE -> {
                                        if (fieldAnnotation.blockWord) {
                                            field.set(any, mString.blockWord())
                                        }
                                    }
                                    TailorLevel.SAFE -> {
                                        if (fieldAnnotation.blockWord) {
                                            field.set(any, safeString.blockWord())
                                        } else {
                                            field.set(any, safeString)
                                        }
                                    }
                                    TailorLevel.TEXT_WITH_BREAK_LINE -> {
                                        if (fieldAnnotation.blockWord) {
                                            field.set(any, textWithBreakLine.blockWord())
                                        } else {
                                            field.set(any, textWithBreakLine)
                                        }
                                    }
                                    TailorLevel.TEXT -> {
                                        if (fieldAnnotation.blockWord) {
                                            field.set(any, textString.blockWord())
                                        } else {
                                            field.set(any, textString)
                                        }
                                    }
                                }
                            } else {
                                if (level != TailorLevel.NONE) newTask(mString)
                                when (level) {
                                    TailorLevel.NONE -> {
                                        if (blockWord) {
                                            field.set(any, mString.blockWord())
                                        }
                                    }
                                    TailorLevel.SAFE -> {
                                        if (blockWord) {
                                            field.set(any, safeString.blockWord())
                                        } else {
                                            field.set(any, safeString)
                                        }
                                    }
                                    TailorLevel.TEXT_WITH_BREAK_LINE -> {
                                        if (blockWord) {
                                            field.set(any, textWithBreakLine.blockWord())
                                        } else {
                                            field.set(any, textWithBreakLine)
                                        }
                                    }
                                    TailorLevel.TEXT -> {
                                        if (blockWord) {
                                            field.set(any, textString.blockWord())
                                        } else {
                                            field.set(any, textString)
                                        }
                                    }
                                }
                            }

                        }

                    }
                    else -> {
                        if (!field.type.isPrimitive) {
                            if (fieldAnnotation != null) {
                                doIt(field.get(any), fieldAnnotation.value, fieldAnnotation.blockWord)
                            } else {
                                doIt(field.get(any), level, blockWord)
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        /**
         * 需要处理的屏蔽词
         * 如果是西文，尽可能的传入全小写字符
         */
        val blockWords = ArrayList<String>()


        /**
         * 屏蔽词被屏蔽后占位的内容
         * 不推荐为空字符串
         */
        var blockWordPatch = "**"
    }
}