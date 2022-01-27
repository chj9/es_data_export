/**
 * 
 */
package com.chenhj.util;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.fastjson.JSONObject;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: SqlParser.java
* @Description: 该类的功能描述
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月21日 上午11:34:43 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月21日     chenhj          v1.0.0               修改原因
*/
public class SqlParser {
	public static String  tableName;
	public static boolean isInsertSql(String sql){
		MySqlStatementParser parser = new MySqlStatementParser(sql);
		SQLStatement statement = parser.parseStatement();
		if(statement instanceof SQLInsertStatement){
			SQLInsertStatement insert =(SQLInsertStatement) statement; 
			tableName = insert.getTableName().toString();
			return true;
		}else if(statement instanceof SQLUpdateStatement){
			SQLUpdateStatement update =(SQLUpdateStatement) statement; 
			tableName = update.getTableName().toString();
			return true;
		}
		return false;
	}
	public static String getTableName() {
		return tableName;
	}
	private static Pattern regex = Pattern.compile("\\#param\\{([^}]*)\\}");
	/**
	 * 获取#param{}中的值
	 * @param str
	 */
	public static Map<String,Integer> getConfigParent(String str){
		Matcher matcher = regex.matcher(str);
		Map<String,Integer> map = new HashMap<>();
		int i =1;
		while(matcher.find()) {
		   map.put(matcher.group(1),i);
		   i++;
		}
		return map;
	}
	/**
	 * 替换#param{}中的值变为?
	 * @param str
	 */
	public static String toLegalSql(String configSql){
		Matcher matcher = regex.matcher(configSql);
		//把符合正则的数据替换成"?"
		configSql=matcher.replaceAll("?");
	    return configSql;
	}
	/**
	 * 替换#param{}中的值变为JSON中对应的key的值
	 * @param str
	 */
	public static String replaceToValue(String configSql,JSONObject json){
		Matcher matcher = regex.matcher(configSql);
		//configSql=matcher.replaceAll("?");
		while(matcher.find()) {
			  String key = matcher.group(1);
			  Object value = json.get(key);
			  if(value instanceof String){
				  configSql=configSql.replace("#param{"+key+"}","'"+value+"'");
			  }else{
				  configSql=configSql.replace("#param{"+key+"}",value+"");
			  }
		}
	    return configSql;
	}
	public static void main(String[] args) {
		//parserInsert("INSERT INTO table_name (phone,imid,aa) VALUES (#param{phone},?,'124');");
		//System.out.println(tableName);
		//System.out.println(sqlFormat("INSERT INTO %s (%s) VALUES (%s);"));
		JSONObject json = new JSONObject();
		json.put("phone",15302789406L);
		json.put("imid","asdfg");
		String sql = "INSERT INTO table_name (phone,aa,imid,aa) VALUES (#param{phone},'nihao',#param{imid},'124');";
		//String sql ="UPDATE table_name SET field1=new-value1, field2=new-value2 WHERE ID = #param{phone}";
		//获得参数的标志位
//		System.out.println(getConfigParent(sql));
//		sql = toLegalSql(sql);
//		//替换标志位的字符
//		System.out.println(sql);
//		//验证sql合法性
//		System.out.println(isInsertSql(sql));
		System.out.println(replaceToValue(sql,json));
	}
}
