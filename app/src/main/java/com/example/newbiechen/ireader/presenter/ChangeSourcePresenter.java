package com.example.newbiechen.ireader.presenter;

import com.example.newbiechen.ireader.model.remote.RemoteRepository;
import com.example.newbiechen.ireader.presenter.contract.ChangeSourceContract;
import com.example.newbiechen.ireader.ui.base.RxPresenter;
import com.example.newbiechen.ireader.utils.LogUtils;
import com.example.newbiechen.ireader.utils.RxUtils;

import io.reactivex.disposables.Disposable;

public class ChangeSourcePresenter extends RxPresenter<ChangeSourceContract.View>
		implements ChangeSourceContract.Presenter {
	@Override
	public void loadSources(String bookId) {
		Disposable disposable = RemoteRepository.getInstance()
				.getBookSources(bookId)
				.compose(RxUtils::toSimpleSingle)
				.subscribe(
						beans -> {
							mView.showSource(beans);
						}
						,
						e -> {
							//TODO: Haven't grate conversation method.
							LogUtils.e(e);
						}
				);
		addDisposable(disposable);
	}
}
