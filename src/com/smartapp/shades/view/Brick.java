package com.smartapp.shades.view;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 长方形方块，自己管理形状位置的变化和界面刷新时机，然后通知场景刷新
 */
public class Brick {

	private float mLeft;
	private float mTop;
	private float mWidth;
	private float mHeight;
	private final Color mColor;
	private Paint mPaint = new Paint();
	private State mState;
	private final ScenesView mParent;
	private final float mDownSpeed;
	private final int mColumns;
	private final int mRows;
	private final int mPosition;
	private final long mTimeGap = 10;

	public Brick(ScenesView parent, Color color, int columns, int rows) {
		mParent = parent;
		mColor = color;
		mColor.initColor();
		mColumns = columns;
		mRows = rows;
		mWidth = parent.getWidth() / columns;
		mHeight = parent.getHeight() / rows;
		mDownSpeed = mHeight / 10.0f;
		mState = State.STRIP;
		mPosition = Util.getRandomIndex(mColumns);
		initStrip();
	}

	public int getPosition() {
		return mPosition;
	}

	public Color getColor() {
		return mColor;
	}

	public State getState() {
		return mState;
	}

	private void initStrip() {
		mLeft = 0;
		mTop = mHeight / 4.0f * (-3.0f);
		mWidth = mParent.getWidth();
		invalidate();
		mParent.post(new Runnable() {

			@Override
			public void run() {
				shorten();
			}
		});
	}

	private void shorten() {
		final float leftPosition = mPosition * mWidth;
		float leftShort = mPosition * mWidth;
		float rightShort = (mColumns - mPosition - 1) * mWidth;
		long shortTime = 50;
		final float leftDistance = leftShort / (shortTime);
		final float rightDistance = rightShort / (shortTime);
		mParent.post(new Runnable() {

			@Override
			public void run() {
				mParent.removeCallbacks(this);
				mLeft += leftDistance;
				mWidth -= rightDistance;
				if (mLeft >= leftPosition) {
					mLeft = leftPosition;
					mWidth = mParent.getWidth() / mColumns;
					invalidate();
					godown();
					return;
				}
				invalidate();
				mParent.postDelayed(this, mTimeGap);
			}
		});
	}

	/**
	 * 方块下落
	 */
	private void godown() {
		mState = State.DOWN;
		List<Brick> list = mParent.getSamePositionBrick(mPosition);
		int count = 0;
		for (Brick brick : list) {
			if (brick != this) {
				count++;
			}
		}
		final float finalTop = (count + 1) * mHeight;
		mParent.post(new Runnable() {

			@Override
			public void run() {
				mParent.removeCallbacks(this);
				mTop += mDownSpeed;
				if (mTop >= finalTop) {
					mTop = finalTop;
					mState = State.STILL;
					invalidate();
					return;
				}
				invalidate();
				mParent.postDelayed(this, mTimeGap);
			}
		});
	}

	/**
	 * 转移位置
	 */
	public void transfer() {
	}

	/**
	 * 合并，颜色会改变，底部不变，高度逐渐从2变变为1倍
	 */
	public void merage() {
		mState = State.MERGE;
		final float finalTop = mTop;
		final float finalHeight = mHeight;
		mTop = mTop - mHeight;
		mHeight = mHeight * 2;
		mColor.nextColor();
		invalidate();
		mParent.post(new Runnable() {

			@Override
			public void run() {
				mParent.removeCallbacks(this);
				mTop += mDownSpeed;
				mHeight -= mDownSpeed;
				if (mTop >= finalTop || mHeight <= finalHeight) {
					mTop = finalTop;
					mHeight = finalHeight;
					mState = State.STILL;
					invalidate();
					return;
				}
				invalidate();
				mParent.postDelayed(this, mTimeGap);
			}
		});
	}

	/**
	 * 销毁
	 */
	public void destory() {
	}

	public void invalidate() {
		mParent.invalidate();
	}

	public void draw(Canvas canvas) {
		mPaint.reset();
		mPaint.setColor(mColor.getColor());
		canvas.drawRect(mLeft, mTop, mLeft + mWidth, mTop + mHeight, mPaint);
	}

}
