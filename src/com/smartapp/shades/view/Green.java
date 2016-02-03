package com.smartapp.shades.view;

public class Green extends Color {
	private int[] array = new int[] { 0xFF111111, 0xFF333333, 0xFF555555,
			0xFF888888, 0xFFaaaaaa, 0xFFcccccc };

	@Override
	public int[] getColorList() {
		return array;
	}

}
