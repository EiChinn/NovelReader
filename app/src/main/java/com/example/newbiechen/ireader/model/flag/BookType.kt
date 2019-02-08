package com.example.newbiechen.ireader.model.flag

enum class BookType(private val typeName: String, private val netName: String) : BookConvert {
    ALL("全部类型", "all"),
    XHQH("玄幻奇幻", "xhqh"),
    WXXX("武侠仙侠", "wxxx"),
    DSYN("都市异能", "dsyn"),
    LSJS("历史军事", "lsjs"),
    YXJJ("游戏竞技", "yxjj"),
    KHLY("科幻灵异", "khly"),
    CYJK("穿越架空", "cyjk"),
    HMZC("豪门总裁", "hmzc"),
    XDYQ("现代言情", "xdyq"),
    GDYQ("古代言情", "gdyq"),
    HXYQ("幻想言情", "hxyq"),
    DMTR("耽美同人", "dmtr");

    override fun getTypeName() = typeName

    override fun getNetName() = netName

}