package com.jeancoder.core.power.support;

public enum DBBrandNameParser {
	/**
	 * 驼峰转下划线 全小写 兼容下划线
	 */
	HUMP_UNDERLINE_LOWERCASE(){
		@Override
		public String parse(String fieldName) {
			StringBuffer columnName = new StringBuffer();
			
			int i = 0;
			for(Character c : fieldName.toCharArray()) {
				if(i == 0) {
					columnName.append(c);
				}else {
					if(Character.isUpperCase(c)) {
						columnName.append("_");
						columnName.append(c);
					}else {
						columnName.append(c);
					}
				}
				i++;
			}
			return columnName.toString().toLowerCase();
		}
	}
	;
	
	abstract public String parse(String fieldName);
}
