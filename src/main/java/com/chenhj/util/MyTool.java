/**
 * 
 */
package com.chenhj.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**   
* Copyright: Copyright (c) 2019 Montnets
* 
* @ClassName: MyTool.java
* @Description: 该类的功能描述
*
* @version: v1.0.0
* @author: chenhj
* @date: 2019年1月16日 下午3:53:27 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2019年1月16日     chenhj          v1.0.0               修改原因
*/
public class MyTool {
	private static Pattern regex = Pattern.compile("\\#\\{([^}]*)\\}");
	/**
	 * 获取#param{}中的值
	 * @param str
	 */
	public static String getConfigParent(String str){
		Matcher matcher = regex.matcher(str);
		String map = null;
		while(matcher.find()) {
		   map=matcher.group(1);
		}
		return map;
	}
	/**
	 * 获取当前String类型的的时间(自定义格式)
	 * @param format  时间格式
	 * @return String
	 */
	public static String getNowTime(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}
}
