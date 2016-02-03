package com.smartapp.shades.view;

public class Green extends Color {
	private int[] array = new int[] { 0xFFdddddd, 0xFFaaaaaa, 0xFF999999,
			0xFF0000FF, 0xFF222222, 0xFFFF0000 };

	@Override
	public int[] getColorList() {
		return array;
	}

}
