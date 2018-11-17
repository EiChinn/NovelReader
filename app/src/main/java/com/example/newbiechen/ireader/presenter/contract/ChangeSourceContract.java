package com.example.newbiechen.ireader.presenter.contract;

import com.example.newbiechen.ireader.model.bean.BookSourcesBean;
import com.example.newbiechen.ireader.ui.base.BaseContract;

import java.util.List;

/**
 * Created by ei_chinn on 17-5-16.
 */

public interface ChangeSourceContract extends BaseContract{
    interface View extends BaseView {
        void showSource(List<BookSourcesBean> bookChapterList);
    }

    interface Presenter extends BasePresenter<View>{
        void loadSources(String bookId);
    }
}
