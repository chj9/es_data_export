/**
 * 
 */
package com.chenhj.util;

import java.security.MessageDigest;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: EncryUtil.java
* @Description: 加密工具类
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月11日 下午4:55:18 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月11日     chenhj          v1.0.0               修改原因
*/
public class EncryUtil {
	private static final char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

	public static String encry(String src, String type) throws Exception{
		try {
			byte[] input = src.getBytes("utf-8");
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest md = MessageDigest.getInstance(type);
			// 使用指定的字节更新摘要
	        md.update(input);
	        // 获得密文
	        input = md.digest();
	        int j = input.length;
	        char str[] = new char[j * 2];
	        int k = 0;
	        for (int i = 0; i < j; i++) {
	            byte byte0 = input[i];
	            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
	            str[k++] = hexDigits[byte0 & 0xf];
	        }
	        return new String(str).toLowerCase();
		} catch (Exception e) {
			throw e;
		}
	}
}
