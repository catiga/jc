package com.jeancoder.core.namer;

import java.util.zip.ZipInputStream;

import com.jeancoder.core.exception.AppFetchException;
import com.jeancoder.core.util.InputStreamUtil;

public class MemoryNamerFetch extends NamerFetch{

	@Override
	public IFetchResult fetch(NamerApplication  application) {
		ZipInputStream zis = null;
		try {
			zis = new ZipInputStream(InputStreamUtil.get(application.getFetchAddress()));
		} catch(Exception e) {
			throw new AppFetchException("LocalNamerFetch fetch failure",e);
		}
		ZipInputStreamFetchResult zisfr = new ZipInputStreamFetchResult();
		zisfr.setFetchResult(zis);
		return zisfr;
	}
}
