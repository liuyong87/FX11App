package com.semisky.multimedia.common.utils;

/**
 * 播放模式
 * 
 * @author liuyong
 * 
 */
public class PlayMode {

	public static final int LOOP = 0;
	public static final int SHUFFLE = 1;
	public static final int SINGLE = 2;

	public static int getDefault() {
		return LOOP;
	}

	public static int switchNextMode(int current) {
		if (current < LOOP || current > SINGLE) {
			return getDefault();
		}

		switch (current) {
		case LOOP:
			return SHUFFLE;
		case SHUFFLE:
			return SINGLE;
		case SINGLE:
			return LOOP;
		}
		return getDefault();
	}

}
