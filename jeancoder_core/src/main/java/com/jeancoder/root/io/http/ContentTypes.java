package com.jeancoder.root.io.http;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class ContentTypes {

	final private static Map<String, String> CONTENT_TYPE_MAPS = new HashMap<>();

	private final static String PATH_CODE = "content-type";
	
	public static final String get(String file) {
		file = file.trim();
		if(!file.startsWith(".")) {
			file = "." + file;
		}
		String value = CONTENT_TYPE_MAPS.get(file);
		if(value==null) {
			value = CONTENT_TYPE_MAPS.get(".*");
		}
		if(value==null) {
			value = "application/octet-stream";
		}
		return value;
	}

	static {
		String path = PATH_CODE;
		String file_path = Thread.currentThread().getContextClassLoader().getResource(path).getFile();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file_path));

			String linecontent = null;
			while ((linecontent = reader.readLine()) != null) {
				linecontent = linecontent.trim();
				int first_space = linecontent.indexOf(" ");
				int last_space = linecontent.lastIndexOf(" ");

				if (first_space > 0 && last_space > 0 && last_space >= first_space) {
					String key = linecontent.substring(0, first_space);
					String value = linecontent.substring(last_space + 1);
					if (CONTENT_TYPE_MAPS.containsKey(key)) {
						// System.out.println("make no sense:" + key);
					}
					CONTENT_TYPE_MAPS.put(key, value);
				}
			}
			reader.close();

			System.out.println(CONTENT_TYPE_MAPS.size());
			CONTENT_TYPE_MAPS.forEach((k, v) -> {
				// System.out.println(k + ":" + v);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
