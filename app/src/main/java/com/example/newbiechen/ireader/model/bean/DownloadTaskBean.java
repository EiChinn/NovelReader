package com.example.newbiechen.ireader.model.bean;

import com.example.newbiechen.ireader.db.entity.BookChapter;

import java.util.List;

/**
 * Created by newbiechen on 17-5-11.
 */
public class DownloadTaskBean{
    public static final int STATUS_LOADING = 1;
    public static final int STATUS_WAIT = 2;
    public static final int STATUS_PAUSE = 3;
    public static final int STATUS_ERROR = 4;
    public static final int STATUS_FINISH = 5;

    //任务名称 -> 名称唯一不重复
    private String taskName;
    //所属的bookId(外健)
    private String bookId;

    private List<BookChapter> bookChapterList;
    //章节的下载进度,默认为初始状态
    private int currentChapter = 0;
    //最后的章节
    private int lastChapter = 0;
    //状态:正在下载、下载完成、暂停、等待、下载错误。

    private volatile int status = STATUS_WAIT;
    //总大小 -> (完成之后才会赋值)
    private long size = 0;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
        if (bookChapterList!=null){
            for (BookChapter bean : bookChapterList){
                bean.setTaskName(getTaskName());
            }
        }
    }

    /**
     * 这才是真正的列表使用类。
     *
     */
    public void setBookChapters(List<BookChapter> beans){
        bookChapterList = beans;
        for (BookChapter bean : bookChapterList){
            bean.setTaskName(getTaskName());
        }
    }

    public List<BookChapter> getBookChapters(){
        return bookChapterList;
    }

    public int getCurrentChapter() {
        return currentChapter;
    }

    public void setCurrentChapter(int current) {
        this.currentChapter = current;
    }

    public int getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(int lastChapter) {
        this.lastChapter = lastChapter;
    }

    //多线程访问的问题，所以需要同步机制
    public int getStatus() {
        return status;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

}
