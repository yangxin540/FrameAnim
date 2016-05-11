package com.juan.frameanimdemo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class FrameTime implements Serializable {

	/**  */
	private static final long serialVersionUID = 1L;
	@SerializedName("start_time")
	private String startTime;
	@SerializedName("end_time")
	private String endTime;
	private List<Folder> folders;

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public List<Folder> getFolders() {
		return folders;
	}

	public void setFolders(List<Folder> folders) {
		this.folders = folders;
	}
}
