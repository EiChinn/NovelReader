package com.example.newbiechen.ireader.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.newbiechen.ireader.model.gen.BookChapterBeanDao;
import com.example.newbiechen.ireader.model.gen.CollBookBeanDao;
import com.example.newbiechen.ireader.model.gen.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

/**
 * Created by newbiechen on 17-5-8.
 * 收藏的书籍
 */
@Entity
public class CollBookBean implements Parcelable {

    public static final int STATUS_UNCACHE = 0; //未缓存
    public static final int STATUS_CACHING = 1; //正在缓存
    public static final int STATUS_CACHED = 2;  //已经缓存
    /**
     * _id : 53663ae356bdc93e49004474
     * title : 逍遥派
     * author : 白马出淤泥
     * shortIntro : 金庸武侠中有不少的神秘高手，书中或提起名字，或不曾提起，总之他们要么留下了绝世秘笈，要么就名震武林。 独孤九剑的创始者，独孤求败，他真的只创出九剑吗？ 残本葵花...
     * cover : /cover/149273897447137
     * hasCp : true
     * latelyFollower : 60213
     * retentionRatio : 22.87
     * updated : 2017-05-07T18:24:34.720Z
     *
     * chaptersCount : 1660
     * lastChapter : 第1659章 朱长老
     */
    @Id
    private String _id; // 本地书籍中，path 的 md5 值作为本地书籍的 id
    private String title;
    private String author;
    private String shortIntro;
    private String cover; // 在本地书籍中，该字段作为本地文件的路径
    private boolean hasCp;
    private int latelyFollower;
    private double retentionRatio;
    //最新更新日期
    private String updated;
    //最新阅读日期
    private String lastRead;
    // 小说章节数， 需要注意的是：这个章节数是mix的章节数而不是源小说的章节数
    private int chaptersCount;
    private String lastChapter;
    //是否更新或未阅读
    private boolean isUpdate = true;
    //是否是本地文件
    private boolean isLocal = false;

    @ToMany(referencedJoinProperty = "bookId")
    private List<BookChapterBean> bookChapterList;

    // 获取目录和章节时使用
    private String currentSourceName;
    private String currentSourceId;



    public CollBookBean() {
    }

    protected CollBookBean(Parcel in) {
        _id = in.readString();
        title = in.readString();
        author = in.readString();
        shortIntro = in.readString();
        cover = in.readString();
        hasCp = in.readByte() != 0;
        latelyFollower = in.readInt();
        retentionRatio = in.readDouble();
        updated = in.readString();
        lastRead = in.readString();
        chaptersCount = in.readInt();
        lastChapter = in.readString();
        isUpdate = in.readByte() != 0;
        isLocal = in.readByte() != 0;
        currentSourceName = in.readString();
        currentSourceId = in.readString();
    }

    @Generated(hash = 485669497)
    public CollBookBean(String _id, String title, String author, String shortIntro, String cover,
            boolean hasCp, int latelyFollower, double retentionRatio, String updated, String lastRead,
            int chaptersCount, String lastChapter, boolean isUpdate, boolean isLocal,
            String currentSourceName, String currentSourceId) {
        this._id = _id;
        this.title = title;
        this.author = author;
        this.shortIntro = shortIntro;
        this.cover = cover;
        this.hasCp = hasCp;
        this.latelyFollower = latelyFollower;
        this.retentionRatio = retentionRatio;
        this.updated = updated;
        this.lastRead = lastRead;
        this.chaptersCount = chaptersCount;
        this.lastChapter = lastChapter;
        this.isUpdate = isUpdate;
        this.isLocal = isLocal;
        this.currentSourceName = currentSourceName;
        this.currentSourceId = currentSourceId;
    }

    public static final Creator<CollBookBean> CREATOR = new Creator<CollBookBean>() {
        @Override
        public CollBookBean createFromParcel(Parcel in) {
            return new CollBookBean(in);
        }

        @Override
        public CollBookBean[] newArray(int size) {
            return new CollBookBean[size];
        }
    };
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1552163441)
    private transient CollBookBeanDao myDao;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getShortIntro() {
        return shortIntro;
    }

    public void setShortIntro(String shortIntro) {
        this.shortIntro = shortIntro;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public boolean isHasCp() {
        return hasCp;
    }

    public void setHasCp(boolean hasCp) {
        this.hasCp = hasCp;
    }

    public int getLatelyFollower() {
        return latelyFollower;
    }

    public void setLatelyFollower(int latelyFollower) {
        this.latelyFollower = latelyFollower;
    }

    public double getRetentionRatio() {
        return retentionRatio;
    }

    public void setRetentionRatio(double retentionRatio) {
        this.retentionRatio = retentionRatio;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public int getChaptersCount() {
        return chaptersCount;
    }

    public void setChaptersCount(int chaptersCount) {
        this.chaptersCount = chaptersCount;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public boolean getHasCp() {
        return this.hasCp;
    }

    public boolean getIsUpdate() {
        return this.isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public String getLastRead() {
        return lastRead;
    }

    public void setLastRead(String lastRead) {
        this.lastRead = lastRead;
    }

    public void setBookChapters(List<BookChapterBean> beans){
        bookChapterList = beans;
        for (BookChapterBean bean : bookChapterList){
            bean.setBookId(get_id());
        }
    }

    public List<BookChapterBean> getBookChapters(){
        if (daoSession == null){
            return bookChapterList;
        } else {
            return getBookChapterList();
        }
    }


    public boolean getIsLocal() {
        return this.isLocal;
    }

    public void setIsLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(shortIntro);
        dest.writeString(cover);
        dest.writeByte((byte) (hasCp ? 1 : 0));
        dest.writeInt(latelyFollower);
        dest.writeDouble(retentionRatio);
        dest.writeString(updated);
        dest.writeString(lastRead);
        dest.writeInt(chaptersCount);
        dest.writeString(lastChapter);
        dest.writeByte((byte) (isUpdate ? 1 : 0));
        dest.writeByte((byte) (isLocal ? 1 : 0));
        dest.writeString(currentSourceName);
        dest.writeString(currentSourceId);
    }

    public String getCurrentSourceName() {
        return this.currentSourceName;
    }

    public void setCurrentSourceName(String currentSourceName) {
        this.currentSourceName = currentSourceName;
    }

    public String getCurrentSourceId() {
        return this.currentSourceId;
    }

    public void setCurrentSourceId(String currentSourceId) {
        this.currentSourceId = currentSourceId;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 711740787)
    public List<BookChapterBean> getBookChapterList() {
        if (bookChapterList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BookChapterBeanDao targetDao = daoSession.getBookChapterBeanDao();
            List<BookChapterBean> bookChapterListNew = targetDao
                    ._queryCollBookBean_BookChapterList(_id);
            synchronized (this) {
                if (bookChapterList == null) {
                    bookChapterList = bookChapterListNew;
                }
            }
        }
        return bookChapterList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1077762221)
    public synchronized void resetBookChapterList() {
        bookChapterList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 159260324)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCollBookBeanDao() : null;
    }


}