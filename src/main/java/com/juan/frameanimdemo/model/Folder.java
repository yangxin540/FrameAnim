package com.juan.frameanimdemo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Folder implements Serializable, Comparable<Folder> {

	/**  */
	private static final long serialVersionUID = 1L;
	private String folder;
	@SerializedName("image_header")
	private String imageHeader;
	private int start;
	private int end;
	@SerializedName("use_local")
	private boolean useLocal;
	@SerializedName("src_url")
	private String url;
	@SerializedName("file_md5")
	private String md5;
	@SerializedName("download_order")
	private int downloadOrder;

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getImageHeader() {
		return imageHeader;
	}

	public void setImageHeader(String imageHeader) {
		this.imageHeader = imageHeader;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public boolean isUseLocal() {
		return useLocal;
	}

	public void setUseLocal(boolean useLocal) {
		this.useLocal = useLocal;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public int getDownloadOrder() {
		return downloadOrder;
	}

	public void setDownloadOrder(int downloadOrder) {
		this.downloadOrder = downloadOrder;
	}

	@Override
	public int compareTo(Folder another) {
		int order = another.getDownloadOrder();
		if (this.downloadOrder == order) {
			return 1;
		} else if (this.downloadOrder > order) {
			return 1;
		}
		return -1;
	}

}
