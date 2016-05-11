package com.juan.frameanimdemo.model;

import java.io.Serializable;
import java.util.List;

public class FrameAnimation implements Serializable {

	/**  */
	private static final long serialVersionUID = 1L;
	private String key;
	private List<FrameTime> times;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<FrameTime> getTimes() {
		return times;
	}

	public void setTimes(List<FrameTime> times) {
		this.times = times;
	}
}
