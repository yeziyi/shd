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

	private OnTouchListener mOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			mGestureDetector.onTouchEvent(event);
			return false;
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
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (w > 0 && h > 0 && mBrickList.size() <= 0) {
			produce();
		}
	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
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
						brick1.disappear();
						brick2.disappear();
						brick3.disappear();
						brick4.disappear();
						for (int j = i + 1; j < mRows; j++) {
							if (list1.size() > j) {
								list1.get(j).disappearDown();
							}
							if (list2.size() > j) {
								list2.get(j).disappearDown();
							}
							if (list3.size() > j) {
								list3.get(j).disappearDown();
							}
							if (list4.size() > j) {
								list4.get(j).disappearDown();
							}
						}
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

	public void removeBrick(Brick brick) {
		brick.destory();
		mBrickList.remove(brick);
		invalidate();
	}

	public List<Brick> getSamePositionBrick(int position) {
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
		Brick brick = new Brick(this, new Green(), mColumns, mRows);
		mCurrentBrick = brick;
		mBrickList.add(brick);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO 向下，加速当前方块
		// TODO 左右，移动当前正在下落的方块
		return false;
	}

}
