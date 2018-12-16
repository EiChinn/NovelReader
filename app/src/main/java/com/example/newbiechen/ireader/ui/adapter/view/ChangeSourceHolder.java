package com.example.newbiechen.ireader.ui.adapter.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newbiechen.ireader.R;
import com.example.newbiechen.ireader.model.bean.BookSourcesBean;
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl;

import java.text.SimpleDateFormat;

public class ChangeSourceHolder extends ViewHolderImpl<BookSourcesBean> {
	private String currentSource;

	public ChangeSourceHolder(String currentSource) {
		this.currentSource = currentSource;
	}

	private ImageView mIvPortrait;
	private TextView mTvSource;
	private TextView mTvUpdateInfo;
	private ImageView mIvCurrentChose;

	@Override
	protected int getItemLayoutId() {
		return R.layout.item_book_source;
	}

	@Override
	public void initView() {
		mIvPortrait = findById(R.id.change_source_iv_portrait);
		mTvSource = findById(R.id.change_source_tv_book_source);
		mTvUpdateInfo = findById(R.id.change_source_tv_update_info);
		mIvCurrentChose = findById(R.id.change_source_iv_current_chose);
	}

	@Override
	public void onBind(BookSourcesBean value, int pos) {

		//头像
//		Glide.with(App.Companion.getInstance())
//				.load(Constant.IMG_BASE_URL + value.getCover())
//				.placeholder(R.drawable.ic_default_portrait)
//				.error(R.drawable.ic_load_error)
//				.fitCenter()
//				.into(mIvPortrait);
		// 源网站
		String source = value.getName();
		mTvSource.setText(source);
		//作者
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mTvUpdateInfo.setText(String.format("%s: %s", dateFormat.format(value.getUpdated()), value.getLastChapter()));
		//简介
		if (source.equals(currentSource)) {
			mIvCurrentChose.setVisibility(View.VISIBLE);
		} else {
			mIvCurrentChose.setVisibility(View.GONE);
		}
	}
}
