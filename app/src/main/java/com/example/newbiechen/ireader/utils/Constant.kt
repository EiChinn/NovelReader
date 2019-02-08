package com.example.newbiechen.ireader.utils


import androidx.annotation.StringDef
import java.io.File
import java.util.*

/**
 * Created by newbiechen on 17-4-16.
 */

object Constant {
    /*SharedPreference*/
    const val SHARED_SEX = "sex"
    const val SHARED_SAVE_BOOK_SORT = "book_sort"
    const val SHARED_SAVE_BILLBOARD = "billboard"
    const val SHARED_CONVERT_TYPE = "convert_type"
    const val SEX_BOY = "male"
    const val SEX_GIRL = "female"

    /*URL_BASE*/
    const val API_BASE_URL = "http://api.zhuishushenqi.com"
    const val IMG_BASE_URL = "http://statics.zhuishushenqi.com"
    //book type
    const val BOOK_TYPE_COMMENT = "normal"
    const val BOOK_TYPE_VOTE = "vote"
    //book state
    const val BOOK_STATE_NORMAL = "normal"
    const val BOOK_STATE_DISTILLATE = "distillate"
    //Book Date Convert Format
    const val FORMAT_BOOK_DATE = "yyyy-MM-dd'T'HH:mm:ss"
    const val FORMAT_TIME = "HH:mm"
    const val FORMAT_FILE_DATE = "yyyy-MM-dd"
    //RxBus
    const val MSG_SELECTOR = 1
    //BookCachePath (因为getCachePath引用了Context，所以必须是静态变量，不能够是静态常量)
    var BOOK_CACHE_PATH = (FileUtils.getCachePath() + File.separator
            + "book_cache" + File.separator)
    //文件阅读记录保存的路径
    var BOOK_RECORD_PATH = (FileUtils.getCachePath() + File.separator
            + "book_record" + File.separator)

    var bookType: Map<String, String> = object : HashMap<String, String>() {
        init {
            put("qt", "其他")
            put(BookType.XHQH, "玄幻奇幻")
            put(BookType.WXXX, "武侠仙侠")
            put(BookType.DSYN, "都市异能")
            put(BookType.LSJS, "历史军事")
            put(BookType.YXJJ, "游戏竞技")
            put(BookType.KHLY, "科幻灵异")
            put(BookType.CYJK, "穿越架空")
            put(BookType.HMZC, "豪门总裁")
            put(BookType.XDYQ, "现代言情")
            put(BookType.GDYQ, "古代言情")
            put(BookType.HXYQ, "幻想言情")
            put(BookType.DMTR, "耽美同人")
        }
    }


    //BookType
    @StringDef(BookType.ALL, BookType.XHQH, BookType.WXXX, BookType.DSYN, BookType.LSJS, BookType.YXJJ, BookType.KHLY, BookType.CYJK, BookType.HMZC, BookType.XDYQ, BookType.GDYQ, BookType.HXYQ, BookType.DMTR)
    @Retention(AnnotationRetention.SOURCE)
    annotation class BookType {
        companion object {
            const val ALL = "all"

            const val XHQH = "xhqh"

            const val WXXX = "wxxx"

            const val DSYN = "dsyn"

            const val LSJS = "lsjs"

            const val YXJJ = "yxjj"
            const val KHLY = "khly"
            const val CYJK = "cyjk"
            const val HMZC = "hmzc"
            const val XDYQ = "xdyq"
            const val GDYQ = "gdyq"
            const val HXYQ = "hxyq"
            const val DMTR = "dmtr"
        }
    }
}
