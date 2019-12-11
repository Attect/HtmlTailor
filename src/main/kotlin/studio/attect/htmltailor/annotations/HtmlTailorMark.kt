package studio.attect.htmltailor.annotations

import studio.attect.htmltailor.TailorLevel

/**
 * 在Bean/Pojo类中给需要裁缝修补的对象、字符串字段打上标记，等待裁缝来处理
 * 如果标记到对象类型字段上，内部所有字段都将继承此规则，但内部也可再指定来覆盖规则
 *
 * [value] 指定裁缝操作到哪一步
 * 设定[blockWord]可以指定是否同时处理屏蔽词
 * @author Attect
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class HtmlTailorMark(val value: TailorLevel = TailorLevel.SAFE, val blockWord: Boolean = true)