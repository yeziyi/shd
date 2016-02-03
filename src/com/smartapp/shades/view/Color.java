package com.smartapp.shades.view;

/**
 * 颜色枚举
 */
public abstract class Color {
	private int mCurrentIndex = 0;

	public void initColor() {
		int len = getColorList().length / 2;
		mCurrentIndex = Util.getRandomIndex(len);
	}

	/**
	 * 颜色列表
	 */
	public abstract int[] getColorList();

	/**
	 * 获取当前的颜色
	 */
	public int getColor() {
		return getColorList()[mCurrentIndex];
	}

	/**
	 * 跳到下一个颜色
	 */
	public int nextColor() {
		mCurrentIndex++;
		if (mCurrentIndex > getColorList().length - 1) {
			mCurrentIndex = getColorList().length - 1;
		}
		return getColor();
	}

	/**
	 * 是否最后一个颜色
	 */
	public boolean isLastColor() {
		return mCurrentIndex >= getColorList().length - 1;
	}

	/**
	 * 是否第一个颜色
	 */
	public boolean isFirstColor() {
		return mCurrentIndex <= 0;
	}

}