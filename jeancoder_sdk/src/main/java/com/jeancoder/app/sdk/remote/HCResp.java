package com.jeancoder.app.sdk.remote;

import com.jeancoder.app.sdk.util.CheckStreamType;

public class HCResp {

	byte[] content;
	
	public byte[] getContent() {
		return content;
	}

	public int getContent_length() {
		return content==null?0:content.length;
	}
	
	public String getOrgContentType() {
		byte[] head = new byte[3];
		if(content!=null&&content.length>3) {
			for(int i=0; i<3; i++) {
				head[i] = content[i];
			}
		} else {
			head[0] = 0;
			head[1] = 0;
			head[2] = 0;
		}
		String content_type = bytesToHexString(head).toUpperCase();
		return content_type;
	}

	public String getContent_type() {
		
		return CheckStreamType.codeToMimeType(getOrgContentType());
	}

	
	private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
