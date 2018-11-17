package com.example.newbiechen.ireader.presenter.contract;

import com.example.newbiechen.ireader.model.bean.BookChapterBean;
import com.example.newbiechen.ireader.ui.base.BaseContract;
import com.example.newbiechen.ireader.widget.page.TxtChapter;

import java.util.List;

/**
 * Created by newbiechen on 17-5-16.
 */

public interface ReadContract extends BaseContract{
    interface View extends BaseContract.BaseView {
        void showCategory(List<BookChapterBean> bookChapterList);
        void finishChapter();
        void errorChapter();
    }

    interface Presenter extends BaseContract.BasePresenter<View>{
        void loadMixCategory(String bookMixId);
        void loadSourceCategory(String bookBookId, String bookMixId);
        void loadChapter(String bookId,List<TxtChapter> bookChapterList);
    }
}
