package studio.attect.htmltailor;

/**
 * 裁缝等级
 * 不同等级的裁缝修补出来的效果不一样
 *
 * @author Attect
 */
public enum TailorLevel {
    /**
     * 什么都不做处理
     */
    NONE,

    /**
     * 修补缺失Dom，修正格式
     */
    SAFE,

    /**
     * 只保留文本和换行
     */
    TEXT_WITH_BREAK_LINE,

    /**
     * 只保留文本
     */
    TEXT
}
