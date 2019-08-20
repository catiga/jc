package com.jeancoder.app.sdk.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckStreamType {
	
	public static void main(String[] arc) {
		//CheckStreamType c = null;
	}
	
	public static String codeToMimeType(String hexcode) {
		List<String> ks = null;
		String ft = null;
		for(String k : _stream_code_.keySet()) {
			if(hexcode.startsWith(k)) {
				ks = _stream_code_.get(k);
			}
		}
		if(ks!=null) {
			ft = ks.get(0);
		}
		ks = _mime_codes_.get(ft);
		if(ks!=null) {
			return ks.get(0);
		}
		return null;
	}

	public static final HashMap<String, List<String>> _stream_code_ = new HashMap<String, List<String>>();

	public static final HashMap<String, List<String>> _mime_codes_ = new HashMap<String, List<String>>();
	static {
		readStreamCode();
		readMimeCode();
	}

	private static void readStreamCode() {
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream("stream_code");
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line = "";
			while ((line = br.readLine()) != null) {
				if(line!=null&&!line.trim().equals("")) {
					String[] arr = line.trim().split(" ");
					if(arr!=null&&arr.length>1) {
						String k = null, v = null;
						for(int i=0; i<arr.length; i++) {
							if(!arr[i].equals("")) {
								if(k==null) {
									k = arr[i].toUpperCase(); continue;
								}
								if(v==null) {
									v = arr[i].toUpperCase(); break;
								}
							}
						}
						if(k!=null&&v!=null) {
							if(_stream_code_.get(k)!=null) {
								_stream_code_.get(k).add(v);
							} else {
								List<String> vs = new ArrayList<>();
								vs.add(v);
								_stream_code_.put(k, vs);
							}
						}
					}
				}
			}
			br.close();
			isr.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void readMimeCode() {
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream("mime_code");
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line = "";
			while ((line = br.readLine()) != null) {
				if(line!=null&&!line.trim().equals("")) {
					String[] arr = line.trim().split("=");
					if(arr!=null&&arr.length>1) {
						String k = arr[0].trim(), v = arr[1].trim();
						k = k.substring("mime.".length()).toUpperCase();
						if(_mime_codes_.get(k)!=null) {
							_mime_codes_.get(k).add(v);
						} else {
							List<String> vs = new ArrayList<>();
							vs.add(v);
							_mime_codes_.put(k, vs);
						}
					}
				}
			}
			br.close();
			isr.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
