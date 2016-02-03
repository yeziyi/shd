package com.smartapp.shades.view;

import java.util.ArrayList;
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
		produce();
	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (Brick brick : mBrickList) {
			brick.draw(canvas);
		}
		for (int i = 0; i < mBrickList.size(); i++) {
			for (int j = i + 1; j < mBrickList.size(); j++) {
				Brick front = mBrickList.get(i);
				Brick back = mBrickList.get(j);
				if ((front.getPosition() == back.getPosition())
						&& (front.getColor().getColor() == back.getColor()
								.getColor())
						&& (!front.getColor().isLastColor())) {
					front.destory();
					mBrickList.remove(front);
					back.merage();
					return;
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
			produce();
		}
	}

	public List<Brick> getSamePositionBrick(int position) {
		List<Brick> list = new ArrayList<Brick>();
		for (Brick brick : mBrickList) {
			if (brick.getPosition() == position) {
				list.add(brick);
			}
		}
		return list;
	}

	private void produce() {
		Brick brick = new Brick(this, new Green(), mColumns, mRows);
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
