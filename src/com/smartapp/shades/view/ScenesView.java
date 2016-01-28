package com.smartapp.shades.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * 游戏场景
 */
public class ScenesView extends View {

	private List<Brick> mBrickList = new ArrayList<Brick>();

	public ScenesView(Context context) {
		super(context);
	}

	public ScenesView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScenesView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (Brick brick : mBrickList) {
			brick.draw(canvas);
		}
	}

}
