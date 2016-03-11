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

	public Brick(ScenesView parent, Color color, int columns, int rows) {
		// TODO 减少计算
		mParent = parent;
		mColor = color;
		mColumns = columns;
		mRows = rows;
		mWidth = parent.getWidth() * 1.0f / columns;
		mHeight = parent.getHeight() * 1.0f / (rows * 1.05f);
		mDownSpeed = mHeight / 12.5f;
		mMoveSpeed = mWidth / 4.0f;
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

	private void godown() {
		List<Brick> list = mParent.getSamePositionBrick(mPosition);
		final float finalTop = mParent.getHeight() - (list.size() * mHeight);
		mParent.post(new Runnable() {

			@Override
			public void run() {
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
		});
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

	public void transfer(int position) {
		// TODO 移动的时候也缓慢下降
		if (mPosition == position) {
			return;
		}
		if (mState == State.DOWN) {
			final float finalLeft = mWidth * position;
			final boolean rightMove = (position > mPosition) ? true : false;
			final float moveSpeed = rightMove ? mMoveSpeed : -mMoveSpeed;
			if (rightMove) {
				for (int i = mPosition + 1; i <= position; i++) {
					List<Brick> list = mParent.getSamePositionBrick(i);
					if (list != null && list.size() > 0) {
						Brick topBrick = list.get(0);
						if (topBrick.getTop() <= mTop + mHeight) {
							transfer(i - 1);
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
							transfer(i + 1);
							return;
						}
					}
				}
			}
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

	public void preDisappearDown() {
		mState = State.PREDISAPPEARDOWN;
		final float finalTop = mTop - mHeight / 4.0f;
		mParent.post(new Runnable() {

			@Override
			public void run() {
				mParent.removeCallbacks(this);
				mTop -= mDownSpeed;
				if (mTop <= finalTop) {
					mTop = finalTop;
					invalidate();
					disappearDown();
					return;
				}
				invalidate();
				mParent.postDelayed(this, mTimeGap);
			}
		});
	}

	private void disappearDown() {
		mState = State.DISAPPEARDOWN;
		final float finalTop = mTop + mHeight + mHeight / 4.0f;
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

	public void preDisappear() {
		mState = State.PREDISAPPEAR;
		final float finalTop = mTop - mHeight / 4.0f;
		mParent.post(new Runnable() {

			@Override
			public void run() {
				mParent.removeCallbacks(this);
				mTop -= mDownSpeed;
				mHeight += mDownSpeed;
				if (mTop <= finalTop) {
					mTop = finalTop;
					invalidate();
					disappear();
					return;
				}
				invalidate();
				mParent.postDelayed(this, mTimeGap);
			}
		});

	}

	private void disappear() {
		mState = State.DISAPPEAR;
		final float finalTop = mTop + mHeight + mHeight / 4.0f;
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

	private void invalidate() {
		mParent.invalidate();
	}

	public void draw(Canvas canvas) {
		mPaint.reset();
		mPaint.setColor(mColor.getColor());
		canvas.drawRect(mLeft, mTop, mLeft + mWidth, mTop + mHeight, mPaint);
	}

}
