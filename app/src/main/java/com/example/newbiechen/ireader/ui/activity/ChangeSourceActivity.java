package com.example.newbiechen.ireader.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.newbiechen.ireader.R;
import com.example.newbiechen.ireader.model.bean.BookSourcesBean;
import com.example.newbiechen.ireader.presenter.ChangeSourcePresenter;
import com.example.newbiechen.ireader.presenter.contract.ChangeSourceContract;
import com.example.newbiechen.ireader.ui.adapter.ChangeSourceAdapter;
import com.example.newbiechen.ireader.ui.base.BaseMVPActivity;

import java.util.List;

public class ChangeSourceActivity extends BaseMVPActivity<ChangeSourceContract.Presenter>
		implements ChangeSourceContract.View {

	private String bookId;
	private String currentSource;
	private RecyclerView rv_book_sources;
	private ChangeSourceAdapter adapter;
	@Override
	protected int getContentId() {
		return R.layout.activity_change_source;
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		super.initData(savedInstanceState);
		if (savedInstanceState != null) {
			bookId = savedInstanceState.getString("book_id");
			currentSource = savedInstanceState.getString("current_source_name");
		} else {
			bookId = getIntent().getStringExtra("book_id");
			currentSource = getIntent().getStringExtra("current_source_name");
		}
	}

	@Override
	protected void initWidget() {
		super.initWidget();
		rv_book_sources = findViewById(R.id.rv_book_sources);
		rv_book_sources.setLayoutManager(new LinearLayoutManager(this));
		adapter = new ChangeSourceAdapter(currentSource);
		rv_book_sources.setAdapter(adapter);

	}

	@Override
	protected void initClick() {
		super.initClick();
		adapter.setOnItemClickListener((view, pos) -> {
			BookSourcesBean source = adapter.getItem(pos);
			Intent intent = new Intent();
			intent.putExtra("current_source_name", source.getName());
			intent.putExtra("current_source_book_id", source.get_id());
			setResult(0xff02, intent);
			finish();
		});
	}

	@Override
	protected void processLogic() {
		super.processLogic();
		mPresenter.loadSources(bookId);
	}

	@Override
	public void showSource(List<BookSourcesBean> bookChapterList) {
		adapter.refreshItems(bookChapterList);

	}

	@Override
	public void showError() {

	}

	@Override
	public void complete() {

	}

	@Override
	protected ChangeSourceContract.Presenter bindPresenter() {
		return new ChangeSourcePresenter();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("book_id", bookId);
		outState.putString("current_source_name", currentSource);
	}
}
