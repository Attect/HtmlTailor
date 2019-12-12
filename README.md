# HTML Tailor

帮助你修补、剔除HTML字符串内容。支持自动处理Bean/Pojo/data class中的字符串内容。

目标是让用户提交的包含任意Html内容在可控范围。

## 基本功能

- 可用于防止XSS攻击（取决与你定义的实际规则）
- 修正错误的HTML语法
- 白名单模式保留HTML tag
- 白名单模式保留HTML tag的属性
- 白名单模式保留HTML tag的class
- 白名单模式保留HTML tag的style
- 白名单模式保留HTML tag中各个属性中的url
- 黑名单模式屏蔽掉屏蔽词
- 处理掉一些危险符号
- 直接丢入一个实例对象无脑自动处理里面的字符串

## 导入项目

跟随以下步骤将此项目引入你的工程中。

1. 克隆此项目源码

2. 执行

   ```Shell
    gradle  assemble
   ```

3. 将 build/libs 下的jar文件导入你的项目中

4. 为你的项目添加最新的 Jsoup 依赖。如果你使用Gradle，可以使用以下配置 

   ```kotlin
   dependencies {
       implementation 'org.jsoup:jsoup:1.12.1'
   }
   ```

   

## 使用方法

### 方式1： 直接操作字符串

例1：使用默认提供的规则处理

```kotlin
val htmlString = "hello, welcome to <a href=\"https://muka.app\" style=\"font-weight:blod\" onclikc=\"alert('see it?')\">Muka</a>"
val tailor = HtmlTailor.defaultTailor
tailor.newTask(htmlString)
println(tailor.safeString)
```

例2：使用自定义规则处理

```kotlin
val htmlString = "hello, welcome to <a href=\"https://muka.app\" style=\"font-weight:blod\" onclikc=\"alert('see it?')\">Muka</a>"
HtmlTailor().apply{
    addDraft("a",OPERATION_KEEP,allowAttributes=arrayOf("style"),allowStyle=arrayOf("color","font-weight"))
    newTask(htmlString)
    println(tailor.safeString)
}
```



### 方式2：直接操作对象实例

更详细的请看源码中的Test

```kotlin
val sampleData = SampleData().apply{
	content = "hello, welcome to <a href=\"https://muka.app\" style=\"font-weight:blod\" onclikc=\"alert('see it?')\">Muka</a>"
	}
HtmlTailor.defaultTailor.apply{
	doIt(sampleData,level=TailorLevel.TEXT,true)
	println(sampleData.toString())
}
```



## 在对象声明中指定doIt自动处理效果

使用注解，在类的成员上做标记，将根据实际注解自动处理。

如果没有指定，将继承自父成员的规则。

```kotlin
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
```

更详细请看并运行项目中的Test代码，然后查看输出看效果



# 屏蔽词处理

因为处理对象中的Html内容和屏蔽词的数据操作几乎一致，为了避免重复的性能开销，加入了屏蔽词处理。

如果屏蔽词为西文字符，将忽略大小写，但推荐你提供的数据都是小写的。



### 添加屏蔽词

```kotlin
HtmlTailor.blockWords.add("fuck")
```



### 设定屏蔽词占位内容

```kotlin
Html.Tailor.blockWordPatch = "[违规内容]"
```

默认为两个星号 ** 

不推荐为空的字符串，否则可能产生意外拼接问题。



### String屏蔽词扩展方法

```kotlin
fun String.blockWord():String
```



### doIt自动处理中的屏蔽词选项

可以通过doIt方法的参数，或者HtmlTailorMark注解的参数指定是否执行词语屏蔽操作，这对排除掉密码等特殊字符串来说非常有效。



# 处理等级

提供四个处理等级，结果类型也不相同。此处的等级，只在doIt方法和HtmlTailMark注解中有效

### NONE

什么都不做。

类型：Html字符串

如果你只是想用它处理屏蔽词，或者跳过一些特殊内容，这是你需要的。



### SAFE

安全等级。

类型：Html字符串

按照你提供的规则处理好的结果。



### TEXT_WITH_BREAK_LINE

仅保留Html的换行内容

类型：Html字符串

只会保留换行和段落部分标签，一些标签也将会被替换。

适合只需要记录换行这一种特殊文本标记的内容。



### TEXT

仅显示文本。

类型：文本

将会只保留可读内容，并且一些特殊符号将会被处理。



# 取结果顺序的接力处理

**HtmlTailor**中有三个字段用于取结果。

**safeString**取值时会根据你提供的规则处理内容

**textWithBreakLine**取值时会将内容只保留换行相关标签

**text**取值时将会只保留文本内容



如果你依次取值这任意多项，后续的取值将会根据前一次的处理进行处理。例如去掉所有div标签后，再获取所有的剩余文本

```kotlin
tailor.safeString
val result = tailor.text
```



**注意**：每一次**newTask**之后，每个取值只会在这之后第一次取值时会进行处理，例如上面这个例子，取值**text**后再取值**safeString**，其结果与之前无差异。



# 自动处理的注意事项

doIt方法自动处理是基于Java反射的。此功能的设计目的仅用于处理简单的data class/Bean/Pojo类。

它的原理是逐层取出内部的对象，一路递归到底，所有字符串字段都将尝试处理(透明和静态将会被跳过)。

因此极度推荐自动处理的对象就应该只是数据对象。如果里面包含了一些高级功能对象，可能导致大量无意义逻辑的执行，甚至因为对象层级间的循环引用导致**堆栈溢出**。

比较推荐的情景是作为后端程序，接收到用户提交的数据后，将数据反实例化后立即使用此功能进行处理。



# 在SpringBoot中自动处理入参

此项目最初设计即为SpringBoot项目准备。但此仓库源码仅包含基本效果的相关逻辑，不包含与Spring的任何东西。

如果你希望Html Tailor自动处理所有用户传入参数，可参考 https://muka.app/?p=313 中的相关内容自行实现相关注解以及Resolver即可。

