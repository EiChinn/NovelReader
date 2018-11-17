package com.example.newbiechen.ireader.model.bean;

import java.util.Date;

public class BookSourcesBean {
	private String _id;
	private String source;
	private String name;
	private String link;
	private String lastChapter;
	private boolean isCharge;
	private int chaptersCount;
	private Date updated;
	private boolean starting;
	private String host;
	public void set_id(String _id) {
		this._id = _id;
	}
	public String get_id() {
		return _id;
	}

	public void setLastChapter(String lastChapter) {
		this.lastChapter = lastChapter;
	}
	public String getLastChapter() {
		return lastChapter;
	}

	public void setLink(String link) {
		this.link = link;
	}
	public String getLink() {
		return link;
	}

	public void setSource(String source) {
		this.source = source;
	}
	public String getSource() {
		return source;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public void setIsCharge(boolean isCharge) {
		this.isCharge = isCharge;
	}
	public boolean getIsCharge() {
		return isCharge;
	}

	public void setChaptersCount(int chaptersCount) {
		this.chaptersCount = chaptersCount;
	}
	public int getChaptersCount() {
		return chaptersCount;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	public Date getUpdated() {
		return updated;
	}

	public void setStarting(boolean starting) {
		this.starting = starting;
	}
	public boolean getStarting() {
		return starting;
	}

	public void setHost(String host) {
		this.host = host;
	}
	public String getHost() {
		return host;
	}
}
