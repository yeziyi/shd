package com.smartapp.shades.view;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 长方形方块
 */
public class Brick {

	private float mLeft;
	private float mTop;
	private float mWidth;
	private float mHeight;

	private int mColor;

	private Paint mPaint = new Paint();

	public void setColor(int color) {
		mColor = color;
	}

	public void setPosition(float left, float top, float width, float height) {
		mLeft = left;
		mTop = top;
		mWidth = width;
		mHeight = height;
	}

	public void draw(Canvas canvas) {
		mPaint.reset();
		mPaint.setColor(mColor);
		canvas.drawRect(mLeft, mTop, mLeft + mWidth, mTop + mHeight, mPaint);
	}
}
