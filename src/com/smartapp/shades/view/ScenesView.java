package com.smartapp.shades.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

/**
 * 游戏场景
 */
public class ScenesView extends View implements OnGestureListener {

	private List<Brick> mBrickList = new ArrayList<Brick>();
	private final int mColumns = 4;
	private final int mRows = 10;
	private GestureDetector mGestureDetector;
	private Brick mCurrentBrick;
	private Preview mPreview;
	private final int FLING_MIN_DISTANCE = 100;
	private final int FLING_MIN_VELOCITY = 200;
	private final int FLING_MIN_DISTANCE_LR = 150;
	private final int FLING_MIN_VELOCITY_LR = 250;

	private OnTouchListener mOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return mGestureDetector.onTouchEvent(event);
		}
	};

	public ScenesView(Context context) {
		super(context);
		init();
	}

	public ScenesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ScenesView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		mGestureDetector = new GestureDetector(getContext(), this);
		this.setOnTouchListener(mOnTouchListener);
		mPreview = new Preview(this);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (w > 0 && h > 0 && mBrickList.size() <= 0) {
			produce();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mPreview != null) {
			mPreview.draw(canvas);
		}
		for (Brick brick : mBrickList) {
			brick.draw(canvas);
		}
		if (mCurrentBrick != null && mCurrentBrick.getState() == State.STILL) {
			List<Brick> slist = getSamePositionBrick(mCurrentBrick
					.getPosition());
			if (slist.size() > 1) {
				if (slist.get(0) == mCurrentBrick) {
					Brick brick = slist.get(1);
					if ((mCurrentBrick.getColor().getColor() == brick
							.getColor().getColor())
							&& (!brick.getColor().isLastColor())
							&& (brick.getState() == State.STILL)) {
						brick.destory();
						mBrickList.remove(brick);
						mCurrentBrick.merage();
					}
				}
			}
		}
		boolean allStill = true;
		for (Brick brick : mBrickList) {
			if (brick.getState() != State.STILL) {
				allStill = false;
				break;
			}
		}
		if (allStill) {
			boolean disappear = false;
			List<Brick> list1 = getSamePositionBrickReverse(0);
			List<Brick> list2 = getSamePositionBrickReverse(1);
			List<Brick> list3 = getSamePositionBrickReverse(2);
			List<Brick> list4 = getSamePositionBrickReverse(3);
			for (int i = 0; i < mRows; i++) {
				if (list1.size() > i && list2.size() > i && list3.size() > i
						&& list4.size() > i) {
					Brick brick1 = list1.get(i);
					Brick brick2 = list2.get(i);
					Brick brick3 = list3.get(i);
					Brick brick4 = list4.get(i);
					if (brick1.getColor().getColor() == brick2.getColor()
							.getColor()
							&& brick2.getColor().getColor() == brick3
									.getColor().getColor()
							&& brick3.getColor().getColor() == brick4
									.getColor().getColor()) {
						List<Brick> preDisappearList = new ArrayList<Brick>();
						List<Brick> preDisappearDownList = new ArrayList<Brick>();
						preDisappearList.add(brick1);
						preDisappearList.add(brick2);
						preDisappearList.add(brick3);
						preDisappearList.add(brick4);
						for (int j = i + 1; j < mRows; j++) {
							if (list1.size() > j) {
								preDisappearDownList.add(list1.get(j));
							}
							if (list2.size() > j) {
								preDisappearDownList.add(list2.get(j));
							}
							if (list3.size() > j) {
								preDisappearDownList.add(list3.get(j));
							}
							if (list4.size() > j) {
								preDisappearDownList.add(list4.get(j));
							}
						}
						preDisappear(preDisappearList, preDisappearDownList);
						disappear = true;
						break;
					}
				} else {
					break;
				}
			}
			if (!disappear) {
				produce();
			}
		}
	}

	private float mCurrentDistance = 0;

	private void preDisappear(final List<Brick> list, final List<Brick> downList) {
		mCurrentDistance = 0;
		if (list == null || list.size() <= 0 || downList == null) {
			return;
		}
		final long timeGap = list.get(0).getTimeGap();
		final float totalDistance = list.get(0).getHeight() / 4.0f;
		final float distance = list.get(0).getDownSpeed();
		post(new Runnable() {

			@Override
			public void run() {
				removeCallbacks(this);
				float tDistance = distance;
				if (mCurrentDistance + tDistance > totalDistance) {
					tDistance = totalDistance - mCurrentDistance;
				}
				mCurrentDistance += tDistance;
				for (Brick brick : list) {
					brick.preDisappear(tDistance);
				}
				for (Brick brick : downList) {
					brick.preDisappearDown(tDistance);
				}
				if (tDistance <= 0) {
					invalidate();
					disappear(list, downList);
					return;
				}
				invalidate();
				postDelayed(this, timeGap);
			}
		});
	}

	private void disappear(final List<Brick> list, final List<Brick> downList) {
		mCurrentDistance = 0;
		if (list == null || list.size() <= 0 || downList == null) {
			return;
		}
		final long timeGap = list.get(0).getTimeGap();
		final float totalDistance = list.get(0).getHeight();
		final float distance = list.get(0).getDownSpeed();
		post(new Runnable() {

			@Override
			public void run() {
				removeCallbacks(this);
				float tDistance = distance;
				if (mCurrentDistance + tDistance > totalDistance) {
					tDistance = totalDistance - mCurrentDistance;
				}
				mCurrentDistance += tDistance;
				for (Brick brick : list) {
					brick.disappear(tDistance);
				}
				for (Brick brick : downList) {
					brick.disappearDown(tDistance);
				}
				if (tDistance <= 0) {
					for (Brick brick : list) {
						brick.destory();
						mBrickList.remove(brick);
					}
					for (Brick brick : downList) {
						brick.disappearDownFinish();
					}
					invalidate();
					return;
				}
				invalidate();
				postDelayed(this, timeGap);
			}
		});
	}

	public void removeBrick(Brick brick) {
		brick.destory();
		mBrickList.remove(brick);
		invalidate();
	}

	public List<Brick> getSamePositionBrick(int position) {
		// TODO 优化性能
		List<Brick> list = new ArrayList<Brick>();
		for (Brick brick : mBrickList) {
			if (brick.getPosition() == position) {
				list.add(brick);
			}
		}
		Collections.sort(list, new Comparator<Brick>() {
			public int compare(Brick b1, Brick b2) {
				if (b1.getTop() > b2.getTop()) {
					return 1;
				} else if (b1.getTop() == b2.getTop()) {
					return 0;
				} else {
					return -1;
				}
			}
		});
		return list;
	}

	public List<Brick> getSamePositionBrickReverse(int position) {
		// TODO 优化性能
		List<Brick> list = new ArrayList<Brick>();
		for (Brick brick : mBrickList) {
			if (brick.getPosition() == position) {
				list.add(brick);
			}
		}
		Collections.sort(list, new Comparator<Brick>() {
			public int compare(Brick b1, Brick b2) {
				if (b1.getTop() > b2.getTop()) {
					return -1;
				} else if (b1.getTop() == b2.getTop()) {
					return 0;
				} else {
					return 1;
				}
			}
		});
		return list;
	}

	private void produce() {
		Color color = mPreview.getColor();
		Brick brick = new Brick(this, color, mColumns, mRows);
		mCurrentBrick = brick;
		mBrickList.add(brick);
		mPreview.setVisible(false);
		mPreview = new Preview(this);
		mPreview.showLater();
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if (mCurrentBrick != null) {
			float x = e.getX();
			float width = mCurrentBrick.getWidth();
			if (x >= 0 && x < width) {
				mCurrentBrick.transfer(0, false);
			} else if (x >= width && x < width * 2.0f) {
				mCurrentBrick.transfer(1, false);
			} else if (x >= width * 2.0f && x < width * 3.0) {
				mCurrentBrick.transfer(2, false);
			} else {
				mCurrentBrick.transfer(3, false);
			}
		}
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (mCurrentBrick != null
				&& (Math.abs(distanceX) > Math.abs(distanceY) * 1.2)) {
			float x = e2.getX();
			float width = mCurrentBrick.getWidth();
			if (x >= 0 && x < width) {
				mCurrentBrick.transfer(0, true);
			} else if (x >= width && x < width * 2.0f) {
				mCurrentBrick.transfer(1, true);
			} else if (x >= width * 2.0f && x < width * 3.0) {
				mCurrentBrick.transfer(2, true);
			} else {
				mCurrentBrick.transfer(3, true);
			}
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (mCurrentBrick != null) {
			if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE_LR
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY_LR) {
				// 向左，不处理
			} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE_LR
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY_LR) {
				// 向右，不处理
			} else if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE
					&& Math.abs(velocityY) > FLING_MIN_VELOCITY) {
				mCurrentBrick.quickDown();
			}
		}
		return true;
	}
}
