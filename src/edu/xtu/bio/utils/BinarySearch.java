package edu.xtu.bio.utils;

public class BinarySearch {
	/**
	 * @author WuQi@XTU
	 * @time_created 2016年3月17日,上午9:59:06
	 * @version 1.0
	 */
	
	public static int search(int[] number,int des,int low) {
		int upper = number.length - 1;
		while (low <= upper) {
			int mid = (low + upper) / 2;
			if (number[mid] < des)
				low = mid + 1;
			else if (number[mid] > des)
				upper = mid - 1;
			else
				return mid;
		}
		return -(++low);
	}

	public static void print(int[] array, String warn) {
		System.out.println(warn);
		int length = array.length;
		for (int i = 0; i < length; i++) {
			System.out.print(array[i] + " ");
		}
		System.out.println();
	}
}
