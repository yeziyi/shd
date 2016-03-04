package com.smartapp.shades.view;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Preview {

	private boolean mVisible = true;
	private ScenesView mParent;
	private final long mTimeGap = 5;
	private Color mColor;
	private float mTransparency;
	private float mLeft;
	private float mTop;
	private float mWidth;
	private float mHeight;
	private Paint mPaint = new Paint();

	public Preview(ScenesView parent) {
		mParent = parent;
		mColor = new Green();
		mColor.initColor();
		mTransparency = 0;
		mLeft = 0;
		mTop = 0;
		mWidth = mParent.getWidth();
		mHeight = parent.getHeight() * 1.0f / (10 * 1.05f) / 4.0f;
	}

	public void showLater() {
		mParent.postDelayed(new Runnable() {

			@Override
			public void run() {
				mParent.removeCallbacks(this);
				if (!mVisible) {
					return;
				}
				mTransparency += 0.05f;
				if (mTransparency >= 1) {
					mTransparency = 1;
					invalidate();
					return;
				}
				invalidate();
				mParent.postDelayed(this, mTimeGap);
			}
		}, 1200);
	}

	public Color getColor() {
		return mColor;
	}

	public void setVisible(boolean visible) {
		mVisible = visible;
	}

	private void invalidate() {
		mParent.invalidate();
	}

	public void draw(Canvas canvas) {
		if (mVisible) {
			mPaint.reset();
			mPaint.setColor(mColor.getColor());
			mPaint.setAlpha((int) (0xFF * mTransparency + 0.5f));
			canvas.drawRect(mLeft, mTop, mLeft + mWidth, mTop + mHeight, mPaint);
		}
	}

}
