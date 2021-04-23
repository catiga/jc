package com.jeancoder.root.server;

public class StringBuilderDemo {
	 
    public static void main(String[] args) throws InterruptedException {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 10; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 1000; j++){
                        stringBuilder.append("a");
                    }
                }
            }).start();
        }
 
        Thread.sleep(100);
        System.out.println(stringBuilder.length());
    }
 
}
