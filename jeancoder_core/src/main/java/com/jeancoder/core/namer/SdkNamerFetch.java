package com.jeancoder.core.namer;

public class SdkNamerFetch extends NamerFetch{

	@Override
	public IFetchResult fetch(NamerApplication application) {
		FilePathFetchResult ifr = new FilePathFetchResult();
		ifr.setFetchResult(application.getFetchAddress());
		return ifr;
	}
 
}
