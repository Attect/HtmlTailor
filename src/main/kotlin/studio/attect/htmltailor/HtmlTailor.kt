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
                    //?.replace("&", "&amp;") //这个符号要在前
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
        drafts.removeIf { draft.tagName == it.tagName }
        drafts.add(draft)
    }

    /**
     * 添加多个裁缝设计图纸
     */
    fun addDrafts(drafts: List<DesignDraft>) {
        drafts.forEach { addDraft(it) }
    }

    /**
     * 添加一个裁缝设计图纸
     */
    fun addDraft(
        tagName: String,
        operation: Int = DesignDraft.OPERATION_KEEP,
        allowAttributes: Array<String>? = null,
        allowClass: Array<String>? = null,
        allowStyle: Array<String>? = null,
        allowUrl: Array<String>? = null,
        replaceTag: String? = null
    ) {
        addDraft(DesignDraft(tagName, operation, allowAttributes, allowClass, allowStyle, allowUrl, replaceTag))
    }

    /**
     * 启用数学公式标签支持
     */
    fun enableMathDraft() {
        addDraft("math")
        addDraft("maction")
        addDraft("menclose")
        addDraft("merror")
        addDraft("mfenced")
        addDraft("mfrac")
        addDraft("mglyph")
        addDraft("mi")
        addDraft("mlabeledtr")
        addDraft("mmultiscripts")
        addDraft("mn")
        addDraft("mo")
        addDraft("mover")
        addDraft("mpadded")
        addDraft("mphantom")
        addDraft("math")
        addDraft("mroot")
        addDraft("mrow")
        addDraft("ms")
        addDraft("mspace")
        addDraft("msqrt")
        addDraft("mstyle")
        addDraft("msub")
        addDraft("msubsup")
        addDraft("msup")
        addDraft("mtable")
        addDraft("mtd")
        addDraft("mtext")
        addDraft("mtr")
        addDraft("munder")
        addDraft("munderover")
        addDraft("semantics")
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
            DesignDraft("section"),
            DesignDraft("article"),
            DesignDraft("aside"),
            DesignDraft("h1"),
            DesignDraft("h2"),
            DesignDraft("h3"),
            DesignDraft("h4"),
            DesignDraft("h5"),
            DesignDraft("h6"),
            DesignDraft("header"),
            DesignDraft("footer"),
            DesignDraft("address"),
            DesignDraft("main"),
            DesignDraft("p"),
            DesignDraft("hr"),
            DesignDraft("pre"),
            DesignDraft("blockquote"),
            DesignDraft("ol"),
            DesignDraft("ul"),
            DesignDraft("li"),
            DesignDraft("dl"),
            DesignDraft("dt"),
            DesignDraft("dd"),
            DesignDraft("figure"),
            DesignDraft("figcaption"),
            DesignDraft("a"),
            DesignDraft("em"),
            DesignDraft("strong"),
            DesignDraft("small"),
            DesignDraft("s"),
            DesignDraft("cite"),
            DesignDraft("q"),
            DesignDraft("dfn"),
            DesignDraft("abbr"),
            DesignDraft("time"),
            DesignDraft("code"),
            DesignDraft("var"),
            DesignDraft("samp"),
            DesignDraft("kbd"),
            DesignDraft("sub"),
            DesignDraft("sup"),
            DesignDraft("i"),
            DesignDraft("b"),
            DesignDraft("u"),
            DesignDraft("mark"),
            DesignDraft("ruby"),
            DesignDraft("rt"),
            DesignDraft("rp"),
            DesignDraft("bdi"),
            DesignDraft("bdo"),
            DesignDraft("span"),
            DesignDraft("br"),
            DesignDraft("wbr"),
            DesignDraft("div", DesignDraft.OPERATION_REPLACE, replaceTag = "p")
        )

        return handleWithConfigs(myConfig)
    }

    private fun handleWithConfigs(config: ArrayList<DesignDraft> = drafts): String {
        document?.allElements?.forEach { element ->
            var isHandled = false
            for (i in config.indices) {
                val designDraft = config[i]
                designDraft.handleElement(element).let {
                    if (!isHandled && it) isHandled = true
                }
                if (designDraft.hasRemoveTag) hasDangerHtmlTag = true
                if (designDraft.hasRemoveAttribute) hasDangerHtmlAttribute = true
                if (designDraft.hasRemoveStyle) hasDangerHtmlStyle = true
                if (isHandled) break
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
                    safeString?.blockWord()
                } else {
                    safeString
                }
            }
            TailorLevel.TEXT_WITH_BREAK_LINE -> {
                return if (blockWord) {
                    textWithBreakLine?.blockWord()
                } else {
                    textWithBreakLine
                }
            }
            TailorLevel.TEXT -> {
                return if (blockWord) {
                    textString?.blockWord()
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
            if (!Modifier.isTransient(field.modifiers) && !Modifier.isStatic(field.modifiers)) {
                val accessible = field.isAccessible
                when (field.type) {
                    String::class.java -> {
                        if (!accessible) field.isAccessible = true
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
                                            field.set(any, safeString?.blockWord())
                                        } else {
                                            field.set(any, safeString)
                                        }
                                    }
                                    TailorLevel.TEXT_WITH_BREAK_LINE -> {
                                        if (fieldAnnotation.blockWord) {
                                            field.set(any, textWithBreakLine?.blockWord())
                                        } else {
                                            field.set(any, textWithBreakLine)
                                        }
                                    }
                                    TailorLevel.TEXT -> {
                                        if (fieldAnnotation.blockWord) {
                                            field.set(any, textString?.blockWord())
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
                                            field.set(any, safeString?.blockWord())
                                        } else {
                                            field.set(any, safeString)
                                        }
                                    }
                                    TailorLevel.TEXT_WITH_BREAK_LINE -> {
                                        if (blockWord) {
                                            field.set(any, textWithBreakLine?.blockWord())
                                        } else {
                                            field.set(any, textWithBreakLine)
                                        }
                                    }
                                    TailorLevel.TEXT -> {
                                        if (blockWord) {
                                            field.set(any, textString?.blockWord())
                                        } else {
                                            field.set(any, textString)
                                        }
                                    }
                                }
                            }

                        }

                    }
                    else -> {
                        if (
                            !field.type.isPrimitive &&
                            field.type != Integer::class.java &&
                            field.type != java.lang.Boolean::class.java &&
                            field.type != java.lang.Double::class.java &&
                            field.type != java.lang.Float::class.java &&
                            field.type != java.lang.Long::class.java &&
                            field.type != java.lang.Byte::class.java &&
                            field.type != java.lang.Short::class.java
                        ) {

                            if (!accessible) field.isAccessible = true
                            if (fieldAnnotation != null) {
                                doIt(field.get(any), fieldAnnotation.value, fieldAnnotation.blockWord)
                            } else {
                                doIt(field.get(any), level, blockWord)
                            }
                        }
                    }
                }
                field.isAccessible = accessible
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

        val defaultTailor by lazy {
            val tailor = HtmlTailor()

            //公共属性
            val commonAttributes = arrayOf(
                //"abbr",
                //"accept",
                //"accept-charset",
                //"accesskey",
                "action",
                "align",
                "alt",
                //"autocomplete",
                //"autosave",
                "axis",
                "bgcolor",
                "border",
                "cellpadding",
                "cellspacing",
                //"challenge",
                //"char",
                //"charoff",
                //"charset",
                //"checked",
                "cite",
                "clear",
                "color",
                "cols",
                "colspan",
                //"compact",
                //"contenteditable",
                //"coords",
                "datetime",
                "dir",
                "disabled",
                //"draggable",
                //"dropzone",
                //"enctype",
                //"for",
                //"frame",
                "headers",
                "height",
                "high",
                //"href",
                //"hreflang",
                "hspace",
                //"ismap",
                //"keytype",
                "label",
                "lang",
                "list",
                "longdesc",
                "low",
                "max",
                "maxlength",
                //"media",
                //"method",
                "min",
                //"multiple",
                "name",
                "nohref",
                "noshade",
                "novalidate",
                "nowrap",
                //"open",
                "optimum",
                //"pattern",
                //"placeholder",
                //"prompt",
                "pubdate",
                //"radiogroup",
                "readonly",
                //"rel",
                //"required",
                //"rev",
                "reversed",
                "rows",
                "rowspan",
                "rules",
                "scope",
                //"selected",
                //"shape",
                "size",
                "span",
                //"spellcheck",
                "src",
                "start",
                //"step",
                "style",
                "summary",
                "tabindex",
                //"target",
                "title",
                //"type",
                //"usemap",
                "valign",
                //"value",
                "vspace",
                "width",
                "wrap"
            )

            //公共Style
            val commonStyle = arrayOf(
                "background",
                "background-attachment",
                "background-clip",
                "background-color",
                "background-image",
                "background-origin",
                "background-position",
                "background-repeat",
                "background-size",
                "border",
                "border-bottom",
                "border-bottom-color",
                "border-bottom-left-radius",
                "border-bottom-right-radius",
                "border-bottom-style",
                "border-bottom-width",
                "border-collapse",
                "border-color",
                "border-image",
                "border-image-outset",
                "border-image-repeat",
                "border-image-slice",
                "border-image-source",
                "border-image-width",
                "border-left",
                "border-left-color",
                "border-left-style",
                "border-left-width",
                "border-radius",
                "border-right",
                "border-right-color",
                "border-right-style",
                "border-right-width",
                "border-spacing",
                "border-style",
                "border-top",
                "border-top-color",
                "border-top-left-radius",
                "border-top-right-radius",
                "border-top-style",
                "border-top-width",
                "border-width",
                "bottom",
                "caption-side",
                "clear",
                "clip",
                "color",
                "content",
                "counter-increment",
                "counter-reset",
                "cursor",
                "direction",
                "display",
                "empty-cells",
                //"float",
                "font",
                "font-family",
                "font-feature-settings",
                "font-kerning",
                "font-language-override",
                "font-size",
                "font-size-adjust",
                "font-stretch",
                "font-style",
                "font-synthesis",
                "font-variant",
                "font-variant-alternates",
                "font-variant-caps",
                "font-variant-east-asian",
                "font-variant-ligatures",
                "font-variant-numeric",
                "font-variant-position",
                "font-weight",
                "height",
                "left",
                "letter-spacing",
                "line-height",
                "list-style",
                "list-style-image",
                "list-style-position",
                "list-style-type",
                "margin",
                "margin-bottom",
                "margin-left",
                "margin-right",
                "margin-top",
                "max-height",
                "max-width",
                "min-height",
                "min-width",
                "opacity",
                "orphans",
                "outline",
                "outline-color",
                "outline-offset",
                "outline-style",
                "outline-width",
                "overflow",
                "overflow-wrap",
                "overflow-x",
                "overflow-y",
                "padding",
                "padding-bottom",
                "padding-left",
                "padding-right",
                "padding-top",
                "page-break-after",
                "page-break-before",
                "page-break-inside",
                "quotes",
                "right",
                "table-layout",
                "text-align",
                "text-decoration",
                "text-decoration-color",
                "text-decoration-line",
                "text-decoration-skip",
                "text-decoration-style",
                "text-indent",
                "text-transform",
                "top",
                "unicode-bidi",
                "vertical-align",
                //"visibility",
                "white-space",
                "widows",
                "width",
                "word-spacing",
                "z-index"
            )

            //
            //以下参考自 @see https://developer.mozilla.org/zh-CN/docs/Web/Guide/HTML/HTML5/HTML5_element_list
            //不采用的将使用注释标记，以便之后对比
            //这只是一份简单列表，对于svg等的相关高级标签并未完全包含
            //region 章节
            tailor.addDraft("section", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("article", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("aside", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("h1", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("h2", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("h3", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("h4", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("h5", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("h6", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("header", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("footer", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("address", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("main", allowAttributes = commonAttributes, allowStyle = commonStyle)
            //endregion

            //region 组织内容
            tailor.addDraft("p", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("hr", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("pre", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("blockquote", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("ol", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("ul", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("li", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("dl", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("dt", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("dd", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("figure", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("figcaption", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("div", allowAttributes = commonAttributes, allowStyle = commonStyle)
            //endregion

            //region 文字形式
            tailor.addDraft("a", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("em", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("strong", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("small", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("s", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("cite", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("q", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("dfn", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("abbr", allowAttributes = commonAttributes, allowStyle = commonStyle)
            //tailor.addDraft("data",allowAttributes = commonAttributes,allowStyle = commonStyle)
            tailor.addDraft("time", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("code", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("var", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("samp", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("kbd", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("sub", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("sup", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("i", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("b", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("u", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("mark", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("ruby", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("rt", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("rp", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("bdi", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("bdo", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("span", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("br", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("wbr", allowAttributes = commonAttributes, allowStyle = commonStyle)
            //endregion

            //region 编辑
            tailor.addDraft("ins", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("del", allowAttributes = commonAttributes, allowStyle = commonStyle)
            //endregion

            //region 嵌入内容
            tailor.addDraft(
                "img",
                allowAttributes = commonAttributes.plus("src"),
                allowStyle = commonStyle,
                allowUrl = arrayOf("//", "data:image/")
            )
            //tailor.addDraft("iframe",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("embed",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("object",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("param",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("video",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("audio",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("source",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("track",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("canvas",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("map",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("area",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("svg",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("math",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //endregion

            //region 表格
            tailor.addDraft("table", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("caption", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("colgroup", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("col", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("tbody", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("thead", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("tfoot", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("tr", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("td", allowAttributes = commonAttributes, allowStyle = commonStyle)
            tailor.addDraft("th", allowAttributes = commonAttributes, allowStyle = commonStyle)
            //endregion

            //region 表单
            //tailor.addDraft("form",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("fieldset",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("legend",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("label",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("input",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("button",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("select",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("datalist",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("optgroup",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("option",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("textarea",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("keygen",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("output",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("progress",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("mater",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //endregion

            //region 交互元素
            //tailor.addDraft("details",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("summary",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("menuitem",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //tailor.addDraft("menu",allowAttributes = commonAttributes,allowStyle = commonStyle)
            //endregion

            tailor
        }
    }
}