package com.smartapp.shades.view;

public class Green extends Color {
	private int[] array = new int[] { 0xFFcccccc, 0xFFaaaaaa, 0xFF888888,
			0xFF555555, 0xFF444444, 0xFF000000 };

	@Override
	public int[] getColorList() {
		return array;
	}

}
