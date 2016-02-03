package com.smartapp.shades.view;

import java.util.Random;

public class Util {

	private static Random sRandom = new Random();

	public static int getRandomIndex(int length) {
		return (Math.abs(sRandom.nextInt()) % length);
	}
}
