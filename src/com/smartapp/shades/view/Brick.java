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
		mWidth = parent.getWidth() * 1.0f / columns;
		mHeight = parent.getHeight() * 1.0f / rows;
		mDownSpeed = mHeight / 12.0f;
		mState = State.STRIP;
		mPosition = Util.getRandomIndex(mColumns);
		mParent.postDelayed(new Runnable() {

			@Override
			public void run() {
				initStrip();
			}
		}, mTimeGap);
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

	public float getTop() {
		return mTop;
	}

	public float getHeight() {
		return mHeight;
	}

	private void initStrip() {
		mLeft = 0;
		mTop = mHeight / 4.0f * (-3.0f);
		mWidth = mParent.getWidth();
		invalidate();
		mParent.postDelayed(new Runnable() {

			@Override
			public void run() {
				shorten();
			}
		}, mTimeGap);
	}

	private void shorten() {
		final float finalWidth = mParent.getWidth() * 1.0f / mColumns;
		final float finalLeft = mPosition * finalWidth;
		float leftShort = mPosition * finalWidth;
		float rightShort = (mColumns - mPosition - 1) * finalWidth;
		long shortTime = 50;
		final float leftDistance = leftShort / (shortTime);
		final float rightDistance = rightShort / (shortTime);
		mParent.post(new Runnable() {

			@Override
			public void run() {
				mParent.removeCallbacks(this);
				mLeft += leftDistance;
				mWidth -= (rightDistance + leftDistance);
				if (mWidth <= finalWidth) {
					mLeft = finalLeft;
					mWidth = finalWidth;
					mState = State.DOWN;
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
		List<Brick> list = mParent.getSamePositionBrick(mPosition);
		final float finalTop = mParent.getHeight() - (list.size() * mHeight);
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
		final float finalTop = mTop + mHeight;
		final float finalHeight = mHeight;
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

	public void destory() {
	}

	public void disappearDown() {
		mState = State.DISAPPEARDOWN;
		final float finalTop = mTop + mHeight;
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

	public void disappear() {
		mState = State.DISAPPEAR;
		final float finalTop = mTop + mHeight;
		mParent.post(new Runnable() {

			@Override
			public void run() {
				mParent.removeCallbacks(this);
				mTop += mDownSpeed;
				mHeight -= mDownSpeed;
				if (mHeight <= 0) {
					mTop = finalTop;
					mHeight = 0;
					mParent.removeBrick(Brick.this);
					invalidate();
					return;
				}
				invalidate();
				mParent.postDelayed(this, mTimeGap);
			}
		});
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
