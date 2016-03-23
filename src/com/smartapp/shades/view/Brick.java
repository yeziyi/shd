package com.smartapp.shades.view;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;

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
	private final float mMoveSpeed;
	private final int mColumns;
	private final int mRows;
	private int mPosition;
	private final long mTimeGap = 16;

	private static float sWidth;
	private static float sHeight;
	private static float sDownSpeed;
	private static float sMoveSpeed;

	public Brick(ScenesView parent, Color color, int columns, int rows) {
		// 减少计算
		mParent = parent;
		mColor = color;
		mColumns = columns;
		mRows = rows;
		if (sWidth <= 0) {
			sWidth = parent.getWidth() * 1.0f / columns;
		}
		if (sHeight <= 0) {
			sHeight = parent.getHeight() * 1.0f / (rows * 1.05f);
		}
		if (sMoveSpeed <= 0) {
			sMoveSpeed = sWidth / 4.0f;
		}
		if (sDownSpeed <= 0) {
			sDownSpeed = sHeight / 12.5f;
		}
		mWidth = sWidth;
		mHeight = sHeight;
		mDownSpeed = sDownSpeed;
		mMoveSpeed = sMoveSpeed;
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

	public float getWidth() {
		return mWidth;
	}

	public float getDownSpeed() {
		return mDownSpeed;
	}

	public long getTimeGap() {
		return mTimeGap;
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

	private Runnable mGoDownRunnable;

	private void godown() {
		List<Brick> list = mParent.getSamePositionBrick(mPosition);
		final float finalTop = mParent.getHeight() - (list.size() * mHeight);
		mParent.removeCallbacks(mGoDownRunnable);
		mGoDownRunnable = new Runnable() {

			@Override
			public void run() {
				mParent.removeCallbacks(mGoDownRunnable);
				mParent.removeCallbacks(this);
				if (mState != State.DOWN) {
					return;
				}
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
		};
		mParent.post(mGoDownRunnable);
	}

	public void quickDown() {
		if (mState != State.DOWN) {
			return;
		}
		List<Brick> list = mParent.getSamePositionBrick(mPosition);
		final float finalTop = mParent.getHeight() - (list.size() * mHeight);
		mState = State.QUICKDOWN;
		mParent.post(new Runnable() {

			@Override
			public void run() {
				mParent.removeCallbacks(this);
				mTop += mDownSpeed;
				mTop += mDownSpeed;
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

	public void transfer(int position, boolean quick) {
		if (mPosition == position) {
			return;
		}
		if (mState == State.DOWN) {
			final boolean rightMove = (position > mPosition) ? true : false;
			if (rightMove) {
				for (int i = mPosition + 1; i <= position; i++) {
					List<Brick> list = mParent.getSamePositionBrick(i);
					if (list != null && list.size() > 0) {
						Brick topBrick = list.get(0);
						if (topBrick.getTop() <= mTop + mHeight) {
							transfer(i - 1, quick);
							return;
						}
					}
				}
			} else {
				for (int i = mPosition - 1; i >= position; i--) {
					List<Brick> list = mParent.getSamePositionBrick(i);
					if (list != null && list.size() > 0) {
						Brick topBrick = list.get(0);
						if (topBrick.getTop() <= mTop + mHeight) {
							transfer(i + 1, quick);
							return;
						}
					}
				}
			}
			final float finalLeft = mWidth * position;
			final float moveSpeed = (quick ? 100 : 1)
					* (rightMove ? mMoveSpeed : -mMoveSpeed);
			mState = State.TRANSFERING;
			mPosition = position;
			mParent.post(new Runnable() {

				@Override
				public void run() {
					mParent.removeCallbacks(this);
					mLeft += moveSpeed;
					if ((rightMove && mLeft >= finalLeft)
							|| (!rightMove && mLeft <= finalLeft)) {
						mLeft = finalLeft;
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
	}

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

	public void preDisappearDown(float distance) {
		mState = State.PREDISAPPEARDOWN;
		mTop -= distance;
	}

	public void disappearDown(float distance) {
		mState = State.DISAPPEARDOWN;
		mTop += distance;
	}

	public void disappearDownFinish() {
		mState = State.STILL;
	}

	public void preDisappear(float distance) {
		mState = State.PREDISAPPEAR;
		mTop -= distance;
		mHeight += distance;
	}

	public void disappear(float distance) {
		mState = State.DISAPPEAR;
		mTop += mDownSpeed;
		mHeight -= mDownSpeed;
	}

	private void invalidate() {
		mParent.invalidate();
	}

	public void draw(Canvas canvas) {
		mPaint.reset();
		mPaint.setColor(mColor.getColor());
		canvas.drawRect(mLeft, mTop, mLeft + mWidth, mTop + mHeight, mPaint);
	}

}
