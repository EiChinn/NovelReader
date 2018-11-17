package com.example.newbiechen.ireader.ui.adapter;

import com.example.newbiechen.ireader.model.bean.BookSourcesBean;
import com.example.newbiechen.ireader.ui.adapter.view.ChangeSourceHolder;
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder;
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter;

public class ChangeSourceAdapter extends WholeAdapter<BookSourcesBean> {
	private String currentSource;

	public ChangeSourceAdapter(String currentSource) {
		this.currentSource = currentSource;
	}

	@Override
	protected IViewHolder<BookSourcesBean> createViewHolder(int viewType) {
		return new ChangeSourceHolder(currentSource);
	}

}