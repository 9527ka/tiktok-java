package util;

import java.util.Random;

public class RandomUtil {

	public static final String numberChar = "0123456789";

	public static int random(int min, int max) {
		Random random = new Random();
		int rand = random.nextInt(max) % (max - min + 1) + min;
		;
		return rand;
	}

	public static String getRandomNum(int num) {
		String number = "";
		for (int i = 0; i < num; i++) {
			int j = (int) (java.lang.Math.random() * 10);
			number += String.valueOf(j);
		}
		return number;
	}

	public static void main(String[] args) {
		System.out.println(RandomUtil.getRandomNum(4));
		System.out.println(RandomUtil.getRandomNum(2));
		System.out.println(RandomUtil.getRandomNum(2));
		System.out.println(RandomUtil.getRandomNum(2));
		System.out.println(RandomUtil.getRandomNum(2));
	}
}
