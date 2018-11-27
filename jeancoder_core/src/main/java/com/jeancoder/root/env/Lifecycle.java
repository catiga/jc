package com.jeancoder.root.env;

public interface Lifecycle {

	final String STATE_READY = "READY";

	final String STATE_INITED = "STATE_INITED";

	final String STATE_STARTING = "STARTING";

	final String STATE_RUNNING = "RUNNING";

	final String STATE_STOPING = "STOPING";

	final String STATE_STOPED = "STOPED";
	
	void onInit();

	void onStart();

	void onStop();

	void onDestroy();

}
