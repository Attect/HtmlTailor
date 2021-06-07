package studio.attect.htmltailor

import org.jsoup.nodes.Element

/**
 * 设计图纸
 * 裁缝根据设计图纸对Html Dom进行处理
 *
 * @author Attect
 */
class DesignDraft(
    val tagName:String,
    val operation:Int = OPERATION_KEEP,
    val allowAttributes:Array<String>? = null,
    val allowClass:Array<String>? = null,
    val allowStyle:Array<String>? = null,
    val allowUrl:Array<String>? = null,
    val replaceTag:String? = null
) {

    /**
     * 此规则是否移除了DOM
     */
    var hasRemoveTag = false

    /**
     * 此规则是否移除了属性
     */
    var hasRemoveAttribute = false

    /**
     * 此规则是否移除了style中的css
     */
    var hasRemoveStyle = false

    /**
     * 此规则是否移除了url
     */
    var hasBlockUrl = false

    /**
     * 处理Element节点
     * 返回是否经过处理
     */
    fun handleElement(element: Element):Boolean{
        if(element.tagName()!= tagName) return false
        when(operation){
            OPERATION_KEEP->{
                handleAttributes(element)
                handleStyle(element)
            }
            OPERATION_REPLACE->{
                element.tagName(replaceTag?:"div")
                handleAttributes(element)
                handleStyle(element)
            }
            OPERATION_REMOVE->{
                hasRemoveTag = true
                element.remove()
            }
        }
        return true
    }

    /**
     * 处理Dom节点各项属性
     */
    private fun handleAttributes(element: Element){
        val allAttrKeys = arrayListOf<String>()
        element.attributes().forEach {
            allAttrKeys.add(it.key)
        }
        allAttrKeys.forEach {
            if (allowAttributes?.contains(it) != true) {
                hasRemoveAttribute = true
                element.removeAttr(it)
            }
        }
        if(element.hasAttr("class") && allowAttributes?.contains("class") == true && !allowClass.isNullOrEmpty()){
            val classNames = element.classNames()
            classNames.removeIf { !allowClass.contains(it) }
            element.classNames(classNames)
        }
        handleAttributeUrl(element,"src")
        handleAttributeUrl(element,"href")
    }

    /**
     * 处理节点属性中的url
     */
    private fun handleAttributeUrl(element: Element,attributeName:String){
        if(element.hasAttr(attributeName) && allowAttributes?.contains(attributeName) == true && !allowUrl.isNullOrEmpty()){
            var isMatch = false
            val url = element.attr(attributeName)
            for (i in allowUrl.indices){
                if(url.startsWith(allowUrl[i])){
                    isMatch = true
                    break
                }
            }
            if(isMatch){
                return
            }
        }

        element.removeAttr(attributeName)
        hasBlockUrl
    }

    /**
     * 处理Style节点中的css内容
     * 处理的前提是允许style属性存在且指定了允许的css内容
     */
    private fun handleStyle(element: Element){
        if (!element.hasAttr("style")) return

        val cssList = ArrayList<Pair<String, String>>()
        element.attr("style").trim().split(";").let { styleList ->
            styleList.forEach {
                if (it.contains(":")) {
                    val splitArray = it.split(":")
                    if (splitArray.size == 2) {
                        val key = splitArray[0].trim().replace(" ", "")
                        val value = splitArray[1].trim()
                        //style中可包含url @see https://developer.mozilla.org/zh-CN/docs/Web/CSS/url
                        var urlCheck = false
                        val checkUrlValue = value.lowercase().replace(" ","").replace("\r","").replace("\n","")
                        if(!allowUrl.isNullOrEmpty() && checkUrlValue.contains("url(")){
                            val cutHeadUrlString = checkUrlValue.replace("\"","").replace("'","").substring(checkUrlValue.indexOf("url(")+4)
                            for(i in allowUrl.indices){
                                if(cutHeadUrlString.startsWith(allowUrl[i])){
                                    urlCheck = true
                                    break
                                }
                            }
                        }else{
                            urlCheck = true
                        }
                        if (allowStyle?.contains(key) == true && urlCheck) {
                            cssList.add(Pair(key, value))
                        }
                    }
                }
            }
            if (cssList.size != styleList.size) hasRemoveStyle = true
        }

        val cssBuilder = StringBuilder()
        cssList.forEach {
            if (cssBuilder.isNotEmpty()) cssBuilder.append(";")
            cssBuilder.append(it.first).append(":").append(it.second)
        }
        if (cssBuilder.length > 1) {
            element.attr("style", cssBuilder.toString())
        } else {
            element.removeAttr("style")
        }
    }

    companion object{
        /**
         * 基本操作：保留
         */
        const val OPERATION_KEEP = 1

        /**
         * 基本操作：替换为另外的标签
         */
        const val OPERATION_REPLACE = 2

        /**
         * 基本操作：彻底移除此标签
         */
        const val OPERATION_REMOVE = 3

    }
}
