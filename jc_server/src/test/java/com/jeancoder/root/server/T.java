package com.jeancoder.root.server;

import java.math.BigDecimal;

public class T {

	public static void main(String[] args) {
		int mill_sec = 98004;

		int sec = BigDecimal.valueOf((Math.ceil(mill_sec/1000))).intValue();

		if(sec<60) {
			System.out.println( "0:" + sec);
		} else {
			int min = BigDecimal.valueOf((Math.floor(sec/60))).intValue();
			int real_sec = sec - 60*min;
			
			System.out.println( min + ":" + real_sec);
		}
	}

}
