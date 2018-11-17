package com.jeancoder.core.namer;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.zip.ZipInputStream;

import com.jeancoder.core.exception.AppFetchException;

public class LocalNamerFetch extends NamerFetch{
	
	@Override
	public IFetchResult fetch(NamerApplication  application) {
		String url = application.getFetchAddress();
		ZipInputStream zis = null;
		try {
			zis = new ZipInputStream(new FileInputStream(new File(new URI(url))));
		} catch(Exception e) {
			throw new AppFetchException("LocalNamerFetch fetch failure",e);
		}
		ZipInputStreamFetchResult zisfr = new ZipInputStreamFetchResult();
		zisfr.setFetchResult(zis);
		return zisfr;
	}
}
