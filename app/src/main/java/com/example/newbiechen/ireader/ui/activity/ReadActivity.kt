package com.example.newbiechen.ireader.ui.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat.LAYER_TYPE_SOFTWARE
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.BindView
import com.example.newbiechen.ireader.BOOK_ID
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.db.entity.BookChapter
import com.example.newbiechen.ireader.db.entity.CollBook
import com.example.newbiechen.ireader.model.local.BookRepository
import com.example.newbiechen.ireader.model.local.ReadSettingManager
import com.example.newbiechen.ireader.presenter.ReadPresenter
import com.example.newbiechen.ireader.presenter.contract.ReadContract
import com.example.newbiechen.ireader.ui.adapter.CategoryAdapter
import com.example.newbiechen.ireader.ui.base.BaseMVPActivity
import com.example.newbiechen.ireader.ui.dialog.ReadSettingDialog
import com.example.newbiechen.ireader.utils.*
import com.example.newbiechen.ireader.widget.page.PageLoader
import com.example.newbiechen.ireader.widget.page.PageView
import com.example.newbiechen.ireader.widget.page.TxtChapter
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_read.*

/**
 * Created by newbiechen on 17-5-16.
 */

class ReadActivity : BaseMVPActivity<ReadContract.View, ReadContract.Presenter>(), ReadContract.View {

    // 注册 Brightness 的 uri
    private val BRIGHTNESS_MODE_URI = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE)
    private val BRIGHTNESS_URI = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS)
    private val BRIGHTNESS_ADJ_URI = Settings.System.getUriFor("screen_auto_brightness_adj")


    @BindView(R.id.read_dl_slide)
    @JvmField internal var mDlSlide: DrawerLayout? = null
    /*************top_menu_view */
    @BindView(R.id.read_abl_top_menu)
    @JvmField internal var mAblTopMenu: AppBarLayout? = null
    @BindView(R.id.read_tv_change_source)
    @JvmField internal var mTvChangeSource: TextView? = null
    @BindView(R.id.read_tv_community)
    @JvmField internal var mTvCommunity: TextView? = null
    @BindView(R.id.read_tv_brief)
    @JvmField internal var mTvBrief: TextView? = null
    /***************content_view */
    @BindView(R.id.read_pv_page)
    @JvmField internal var mPvPage: PageView? = null
    /***************bottom_menu_view */
    @BindView(R.id.read_tv_page_tip)
    @JvmField internal var mTvPageTip: TextView? = null

    @BindView(R.id.read_ll_bottom_menu)
    @JvmField internal var mLlBottomMenu: LinearLayout? = null
    @BindView(R.id.read_tv_pre_chapter)
    @JvmField internal var mTvPreChapter: TextView? = null
    @BindView(R.id.read_sb_chapter_progress)
    @JvmField internal var mSbChapterProgress: SeekBar? = null
    @BindView(R.id.read_tv_next_chapter)
    @JvmField internal var mTvNextChapter: TextView? = null
    @BindView(R.id.read_tv_category)
    @JvmField internal var mTvCategory: TextView? = null
    @BindView(R.id.read_tv_night_mode)
    @JvmField internal var mTvNightMode: TextView? = null
    /*    @BindView(R.id.read_tv_download)
        TextView mTvDownload;*/
    @BindView(R.id.read_tv_setting)
    @JvmField internal var mTvSetting: TextView? = null
    /*****************view */
    private var mSettingDialog: ReadSettingDialog? = null
    private var mPageLoader: PageLoader? = null
    private var mTopInAnim: Animation? = null
    private var mTopOutAnim: Animation? = null
    private var mBottomInAnim: Animation? = null
    private var mBottomOutAnim: Animation? = null
    private var mCategoryAdapter: CategoryAdapter? = null
    private var mCollBook: CollBook? = null
    //控制屏幕常亮
    private var mWakeLock: PowerManager.WakeLock? = null
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {
                WHAT_CATEGORY -> rv_category!!.scrollToPosition(mPageLoader!!.chapterPos)
                WHAT_CHAPTER -> mPageLoader!!.openChapter()
            }
        }
    }
    // 接收电池信息和时间更新的广播
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                val level = intent.getIntExtra("level", 0)
                mPageLoader!!.updateBattery(level)
            } else if (intent.action == Intent.ACTION_TIME_TICK) {
                mPageLoader!!.updateTime()
            }// 监听分钟的变化
        }
    }

    // 亮度调节监听
    // 由于亮度调节没有 Broadcast 而是直接修改 ContentProvider 的。所以需要创建一个 Observer 来监听 ContentProvider 的变化情况。
    private val mBrightObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            onChange(selfChange, null)
        }

        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange)

            // 判断当前是否跟随屏幕亮度，如果不是则返回
            if (selfChange || !mSettingDialog!!.isBrightFollowSystem()) return

            // 如果系统亮度改变，则修改当前 Activity 亮度
            if (BRIGHTNESS_MODE_URI == uri) {
                Log.d(TAG, "亮度模式改变")
            } else if (BRIGHTNESS_URI == uri && !BrightnessUtils.isAutoBrightness(this@ReadActivity)) {
                Log.d(TAG, "亮度模式为手动模式 值改变")
                BrightnessUtils.setBrightness(this@ReadActivity, BrightnessUtils.getScreenBrightness(this@ReadActivity))
            } else if (BRIGHTNESS_ADJ_URI == uri && BrightnessUtils.isAutoBrightness(this@ReadActivity)) {
                Log.d(TAG, "亮度模式为自动模式 值改变")
                BrightnessUtils.setDefaultBrightness(this@ReadActivity)
            } else {
                Log.d(TAG, "亮度调整 其他")
            }
        }
    }

    /***************params */
    private var isCollected = false // isFromSDCard
    private var isNightMode = false
    private var isFullScreen = false
    private var isRegistered = false

    private var mBookId: String? = null
    private var mBookSourceId: String? = null
    private var isSourceId = true

    private var isChangeSource: Boolean = false

    override fun getContentId(): Int {
        return R.layout.activity_read
    }

    override fun bindPresenter(): ReadContract.Presenter {
        return ReadPresenter()
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        mCollBook = intent.getSerializableExtra(EXTRA_COLL_BOOK) as CollBook?
        isCollected = intent.getBooleanExtra(EXTRA_IS_COLLECTED, false)
        isNightMode = ReadSettingManager.isNightMode()
        isFullScreen = ReadSettingManager.isFullScreen()

        mBookSourceId = mCollBook!!.currentSourceId
        if (TextUtils.isEmpty(mBookSourceId)) {
            isSourceId = false
            mBookSourceId = mCollBook!!.bookId
        }
        mBookId = mCollBook!!.bookId
    }

    override fun setUpToolbar(toolbar: Toolbar) {
        super.setUpToolbar(toolbar)
        //设置标题
        toolbar.title = mCollBook!!.title
        //半透明化StatusBar
        SystemBarUtils.transparentStatusBar(this)
    }

    override fun initWidget() {
        super.initWidget()

        // 如果 API < 18 取消硬件加速
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mPvPage!!.setLayerType(LAYER_TYPE_SOFTWARE, null)
        }

        //获取页面加载器
        mPageLoader = mPvPage!!.getPageLoader(mCollBook!!)
        //禁止滑动展示DrawerLayout
        mDlSlide!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        //侧边打开后，返回键能够起作用
        mDlSlide!!.isFocusableInTouchMode = false
        mSettingDialog = ReadSettingDialog(this, mPageLoader!!)

        //夜间模式按钮的状态
        toggleNightMode()

        //注册广播
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        intentFilter.addAction(Intent.ACTION_TIME_TICK)
        registerReceiver(mReceiver, intentFilter)

        //设置当前Activity的Brightness
        if (ReadSettingManager.isBrightnessAuto()) {
            BrightnessUtils.setDefaultBrightness(this)
        } else {
            BrightnessUtils.setBrightness(this, ReadSettingManager.getBrightness())
        }

        //初始化屏幕常亮类
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "NovelReader:keep bright")

        //隐藏StatusBar
        mPvPage!!.post { hideSystemBar() }

        //初始化TopMenu
        initTopMenu()

        //初始化BottomMenu
        initBottomMenu()
    }

    private fun initTopMenu() {
        if (Build.VERSION.SDK_INT >= 19) {
            mAblTopMenu!!.setPadding(0, ScreenUtils.getStatusBarHeight(), 0, 0)
        }
    }

    private fun initBottomMenu() {
        //判断是否全屏
        if (ReadSettingManager.isFullScreen()) {
            //还需要设置mBottomMenu的底部高度
            val params = mLlBottomMenu!!.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = ScreenUtils.getNavigationBarHeight()
            mLlBottomMenu!!.layoutParams = params
        } else {
            //设置mBottomMenu的底部距离
            val params = mLlBottomMenu!!.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = 0
            mLlBottomMenu!!.layoutParams = params
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Log.d(TAG, "onWindowFocusChanged: " + mAblTopMenu!!.measuredHeight)
    }

    private fun toggleNightMode() {
        if (isNightMode) {
            mTvNightMode!!.text = StringUtils.getString(R.string.nb_mode_morning)
            val drawable = ContextCompat.getDrawable(this, R.drawable.ic_read_menu_morning)
            mTvNightMode!!.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        } else {
            mTvNightMode!!.text = StringUtils.getString(R.string.nb_mode_night)
            val drawable = ContextCompat.getDrawable(this, R.drawable.ic_read_menu_night)
            mTvNightMode!!.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        }
    }

    // 注册亮度观察者
    private fun registerBrightObserver() {
        try {
            if (mBrightObserver != null) {
                if (!isRegistered) {
                    val cr = contentResolver
                    cr.unregisterContentObserver(mBrightObserver)
                    cr.registerContentObserver(BRIGHTNESS_MODE_URI, false, mBrightObserver)
                    cr.registerContentObserver(BRIGHTNESS_URI, false, mBrightObserver)
                    cr.registerContentObserver(BRIGHTNESS_ADJ_URI, false, mBrightObserver)
                    isRegistered = true
                }
            }
        } catch (throwable: Throwable) {
            LogUtils.e(TAG, "register mBrightObserver error! ", throwable)
        }

    }

    //解注册
    private fun unregisterBrightObserver() {
        try {
            if (mBrightObserver != null) {
                if (isRegistered) {
                    contentResolver.unregisterContentObserver(mBrightObserver)
                    isRegistered = false
                }
            }
        } catch (throwable: Throwable) {
            LogUtils.e(TAG, "unregister BrightnessObserver error! ", throwable)
        }

    }

    override fun initClick() {
        super.initClick()

        mPageLoader!!.setOnPageChangeListener(
                object : PageLoader.OnPageChangeListener {

                    override fun onChapterChange(pos: Int) {
                        mPageLoader!!.mChapterList!!.forEachIndexed { index, txtChapter ->
                            txtChapter.isSelected = pos == index
                        }
                    }

                    override fun requestChapters(requestChapters: List<TxtChapter>) {
                        mPresenter.loadChapter(mBookId!!, requestChapters)
                        mHandler.sendEmptyMessage(WHAT_CATEGORY)
                        //隐藏提示
                        mTvPageTip!!.visibility = GONE
                    }

                    override fun onCategoryFinish(chapters: List<TxtChapter>?) {
                        chapters!![mPageLoader!!.chapterPos].isSelected = true
                        mCategoryAdapter?.setNewData(chapters)
                    }

                    override fun onPageCountChange(count: Int) {
                        mSbChapterProgress!!.max = Math.max(0, count - 1)
                        mSbChapterProgress!!.progress = 0
                        // 如果处于错误状态，那么就冻结使用
                        mSbChapterProgress!!.isEnabled = !(mPageLoader!!.pageStatus == PageLoader.STATUS_LOADING || mPageLoader!!.pageStatus == PageLoader.STATUS_ERROR)
                    }

                    override fun onPageChange(pos: Int) {
                        mSbChapterProgress!!.post { mSbChapterProgress!!.progress = pos }
                    }
                }
        )

        mSbChapterProgress!!.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        if (mLlBottomMenu!!.visibility == VISIBLE) {
                            //显示标题
                            mTvPageTip!!.text = (progress + 1).toString() + "/" + (mSbChapterProgress!!.max + 1)
                            mTvPageTip!!.visibility = VISIBLE
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {}

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        //进行切换
                        val pagePos = mSbChapterProgress!!.progress
                        if (pagePos != mPageLoader!!.pagePos) {
                            mPageLoader!!.skipToPage(pagePos)
                        }
                        //隐藏提示
                        mTvPageTip!!.visibility = GONE
                    }
                }
        )

        mPvPage!!.setTouchListener(object : PageView.TouchListener {
            override fun onTouch(): Boolean {
                return !hideReadMenu()
            }

            override fun center() {
                toggleMenu(true)
            }

            override fun prePage() {}

            override fun nextPage() {}

            override fun cancel() {}
        })

        mTvCategory!!.setOnClickListener {
            openChapterDrawer()
        }
        mTvSetting!!.setOnClickListener { v ->
            toggleMenu(false)
            mSettingDialog!!.show()
        }

        mTvPreChapter!!.setOnClickListener {
            if (mPageLoader!!.skipPreChapter()) {
                mPageLoader!!.mChapterList!![mPageLoader!!.chapterPos].isSelected = true
                mPageLoader!!.mChapterList!![mPageLoader!!.chapterPos + 1].isSelected = false
            }
        }

        mTvNextChapter!!.setOnClickListener { v ->
            if (mPageLoader!!.skipNextChapter()) {
                mPageLoader!!.mChapterList!![mPageLoader!!.chapterPos].isSelected = true
                mPageLoader!!.mChapterList!![mPageLoader!!.chapterPos - 1].isSelected = false
            }
        }

        mTvNightMode!!.setOnClickListener { v ->
            isNightMode = !isNightMode
            mPageLoader!!.setNightMode(isNightMode)
            toggleNightMode()
        }

        mTvBrief!!.setOnClickListener { v -> BookDetailActivity.startActivity(this, mBookId!!) }

        mTvCommunity!!.setOnClickListener { v ->
            val intent = Intent(this, CommunityActivity::class.java)
            startActivity(intent)
        }

        mTvChangeSource!!.setOnClickListener { v ->
            val intent = Intent(this, ChangeSourceActivity::class.java)
            intent.putExtra(BOOK_ID, mBookId)
            intent.putExtra(ChangeSourceActivity.CURRENT_SOURCE_NAME, mCollBook!!.currentSourceName)
            startActivityForResult(intent, ChangeSourceActivity.REQUEST_CODE)
        }

        mSettingDialog!!.setOnDismissListener { dialog -> hideSystemBar() }

        fab_refresh.setOnClickListener {
            loadCategory()
        }
    }

    /**
     * 隐藏阅读界面的菜单显示
     *
     * @return 是否隐藏成功
     */
    private fun hideReadMenu(): Boolean {
        hideSystemBar()
        if (mAblTopMenu!!.visibility == VISIBLE) {
            toggleMenu(true)
            return true
        } else if (mSettingDialog!!.isShowing) {
            mSettingDialog!!.dismiss()
            return true
        }
        return false
    }

    private fun showSystemBar() {
        //显示
        SystemBarUtils.showUnStableStatusBar(this)
        if (isFullScreen) {
            SystemBarUtils.showUnStableNavBar(this)
        }
    }

    private fun hideSystemBar() {
        //隐藏
        SystemBarUtils.hideStableStatusBar(this)
        if (isFullScreen) {
            SystemBarUtils.hideStableNavBar(this)
        }
    }

    /**
     * 切换菜单栏的可视状态
     * 默认是隐藏的
     */
    private fun toggleMenu(hideStatusBar: Boolean) {
        initMenuAnim()

        if (mAblTopMenu!!.visibility == View.VISIBLE) {
            //关闭
            mAblTopMenu!!.startAnimation(mTopOutAnim)
            mLlBottomMenu!!.startAnimation(mBottomOutAnim)
            mAblTopMenu!!.visibility = GONE
            mLlBottomMenu!!.visibility = GONE
            mTvPageTip!!.visibility = GONE

            if (hideStatusBar) {
                hideSystemBar()
            }
        } else {
            mAblTopMenu!!.visibility = View.VISIBLE
            mLlBottomMenu!!.visibility = View.VISIBLE
            mAblTopMenu!!.startAnimation(mTopInAnim)
            mLlBottomMenu!!.startAnimation(mBottomInAnim)

            showSystemBar()
        }
    }

    //初始化菜单动画
    private fun initMenuAnim() {
        if (mTopInAnim != null) return

        mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in)
        mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out)
        mBottomInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in)
        mBottomOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out)
        //退出的速度要快
        mTopOutAnim!!.duration = 200
        mBottomOutAnim!!.duration = 200
    }

    override fun processLogic() {
        super.processLogic()
        // 如果是已经收藏的，那么就从数据库中获取目录
        if (isCollected) {
            val disposable = BookRepository.instance
                    .getBookChaptersInRx(mBookId!!)
                    .compose<List<BookChapter>> { RxUtils.toSimpleSingle(it) }
                    .subscribe { bookChapter, throwable ->
                        // 如果数据库的目录章节为空，或者小说并被标记更新的，则从网络下载目录
                        if (bookChapter.isEmpty() || (mCollBook!!.isUpdate && !mCollBook!!.isLocal)) {
                            loadCategory()
                            if (throwable != null) {
                                LogUtils.e(throwable.toString())
                            }
                        } else {
                            // 设置 CollBook
                            mPageLoader!!.bookChapters = bookChapter
                            // 刷新章节列表
                            mPageLoader!!.refreshChapterList()
                        }
                    }
            addDisposable(disposable)
        } else {
            // 从网络中获取目录
            loadCategory()
        }
    }

    private fun loadCategory() {
        if (isSourceId) {
            mPresenter.loadSourceCategory(mBookSourceId!!, mBookId!!)
        } else {
            mPresenter.loadMixCategory(mBookId!!)
        }
    }

    /***************************view */
    override fun showError() {

    }

    override fun complete() {

    }

    override fun showCategory(bookChapters: List<BookChapter>) {
        mPageLoader!!.bookChapters = bookChapters
        mPageLoader!!.refreshChapterList()

        // TODO: 2018/11/18 这里几乎每次都要更新数据库，而且是先删除所有旧章节在插入新的，需优化
        // 如果是目录更新的情况，那么就需要存储更新数据
        if (isChangeSource) {
            isChangeSource = false
            BookRepository.instance
                    .resetBookChaptersWithAsync(mBookId!!, bookChapters)
        } else if (isCollected) {
            BookRepository.instance
                    .resetBookChaptersWithAsync(mBookId!!, bookChapters)
        }
    }

    private fun openChapterDrawer() {
        if (mCategoryAdapter == null) {
            initCategoryListView()
        }
        if (mCategoryAdapter!!.itemCount > 0) {
            mCategoryAdapter?.notifyDataSetChanged()
            //移动到指定位置
            rv_category.scrollToPosition(mPageLoader!!.chapterPos)
        }
        //切换菜单
        toggleMenu(true)
        //打开侧滑动栏
        mDlSlide!!.openDrawer(GravityCompat.START)

    }

    private fun initCategoryListView() {
        rv_category.layoutManager = LinearLayoutManager(this)
        mCategoryAdapter = CategoryAdapter(mPageLoader!!.mChapterList!!)
        mCategoryAdapter!!.setOnItemClickListener { _, _, position ->
            mDlSlide!!.closeDrawer(GravityCompat.START)
            mPageLoader!!.skipToChapter(position)
        }
        val headerView = layoutInflater.inflate(R.layout.item_category_header, null, false)
        mCategoryAdapter!!.setHeaderView(headerView)
        rv_category.adapter = mCategoryAdapter
    }

    override fun finishChapter() {
        if (mPageLoader!!.pageStatus == PageLoader.STATUS_LOADING) {
            mHandler.sendEmptyMessage(WHAT_CHAPTER)
        }
    }

    override fun errorChapter() {
        if (mPageLoader!!.pageStatus == PageLoader.STATUS_LOADING) {
            mPageLoader!!.chapterError()
        }
    }

    override fun onBackPressed() {
        if (mAblTopMenu!!.visibility == View.VISIBLE) {
            // 非全屏下才收缩，全屏下直接退出
            if (!ReadSettingManager.isFullScreen()) {
                toggleMenu(true)
                return
            }
        } else if (mSettingDialog!!.isShowing) {
            mSettingDialog!!.dismiss()
            return
        } else if (mDlSlide!!.isDrawerOpen(GravityCompat.START)) {
            mDlSlide!!.closeDrawer(GravityCompat.START)
            return
        }

        if (!mCollBook!!.isLocal && !isCollected) {
            val alertDialog = AlertDialog.Builder(this)
                    .setTitle("加入书架")
                    .setMessage("喜欢本书就加入书架吧")
                    .setPositiveButton("确定") { dialog, which ->
                        //设置为已收藏
                        isCollected = true
                        //设置阅读时间
                        mCollBook!!.lastRead = StringUtils.dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE)

                        BookRepository.instance
                                .insertOrUpdateCollBook(mCollBook!!)

                        exit()
                    }
                    .setNegativeButton("取消") { dialog, which -> exit() }.create()
            alertDialog.show()
        } else {
            exit()
        }
    }

    // 退出
    private fun exit() {
        // 返回给BookDetail。
        val result = Intent()
        result.putExtra(BookDetailActivity.RESULT_IS_COLLECTED, isCollected)
        setResult(Activity.RESULT_OK, result)
        // 退出
        super.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
        registerBrightObserver()
    }

    override fun onResume() {
        super.onResume()
        mWakeLock!!.acquire()
    }

    override fun onPause() {
        super.onPause()
        mWakeLock!!.release()
        if (isCollected) {
            mPageLoader!!.saveRecord()
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterBrightObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)

        mHandler.removeMessages(WHAT_CATEGORY)
        mHandler.removeMessages(WHAT_CHAPTER)

        mPageLoader!!.closeBook()
        mPageLoader = null
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val isVolumeTurnPage = ReadSettingManager.isVolumeTurnPage()
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> if (isVolumeTurnPage) {
                return mPageLoader!!.skipToPrePage()
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> if (isVolumeTurnPage) {
                return mPageLoader!!.skipToNextPage()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        SystemBarUtils.hideStableStatusBar(this)
        if (requestCode == REQUEST_MORE_SETTING) {
            val fullScreen = ReadSettingManager.isFullScreen()
            if (isFullScreen != fullScreen) {
                isFullScreen = fullScreen
                // 刷新BottomMenu
                initBottomMenu()
            }

            // 设置显示状态
            if (isFullScreen) {
                SystemBarUtils.hideStableNavBar(this)
            } else {
                SystemBarUtils.showStableNavBar(this)
            }
        } else if (resultCode == ChangeSourceActivity.RESULT_CODE) {
            val currentSourceName = data!!.getStringExtra(ChangeSourceActivity.CURRENT_SOURCE_NAME)
            val currentSourceBookId = data.getStringExtra(ChangeSourceActivity.CURRENT_SOURCE_BOOK_ID)
            if (!TextUtils.isEmpty(currentSourceBookId)) {
                mBookSourceId = currentSourceBookId
                isSourceId = true
                isChangeSource = true
                mCollBook!!.currentSourceId = currentSourceBookId
                mCollBook!!.currentSourceName = currentSourceName
                BookRepository.instance.changeBookSource(mCollBook!!)
                // 换源之后要清除BookRecord表，以防有换源之后的总章节数小于当前阅读章节数导致IndexOutOfBoundsException
                //                BookRepository.getInstance().deleteBookRecord(mBookId); // 退出阅读时也会更新BookRecord表
            }
        }
    }

    companion object {
        private const val TAG = "ReadActivity"
        const val REQUEST_MORE_SETTING = 1
        const val EXTRA_COLL_BOOK = "extra_coll_book"
        const val EXTRA_IS_COLLECTED = "extra_is_collected"

        private const val WHAT_CATEGORY = 1
        private const val WHAT_CHAPTER = 2

        fun startActivity(context: Context, collBook: CollBook, isCollected: Boolean) {
            context.startActivity(Intent(context, ReadActivity::class.java)
                    .putExtra(EXTRA_IS_COLLECTED, isCollected)
                    .putExtra(EXTRA_COLL_BOOK, collBook))
        }
    }
}
