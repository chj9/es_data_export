/**
 * 
 */
package com.chenhj.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

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
	private static String tableName;
	private static List<String> columnList;
	private static List<Object> valueList;
	public static Map<String,Object> parserInsert(String sql){
		MySqlStatementParser parser = new MySqlStatementParser(sql);
		SQLStatement statement = parser.parseStatement();
		MySqlInsertStatement insert = (MySqlInsertStatement)statement;
		Map<String,Object> map  = new LinkedHashMap<>();
		List<SQLExpr> columns = insert.getColumns();  // 获得所有列名
		List<SQLExpr> valuse = insert.getValues().getValues();
		int size = columns.size();
		columnList = new ArrayList<>();
		valueList = new ArrayList<>();
		for(int i=0;i<size;i++){
			SQLExpr sqlco =columns.get(i);
			map.put(sqlco.toString(), valuse.get(i));
			columnList.add(sqlco.toString());
			valueList.add(valuse.get(i));
		}
		tableName = insert.getTableName().toString();
		return map;
	}
	public static String getTableName() {
		return tableName;
	}
	public static List<String> getColumnList() {
		return columnList;
	}
	public static List<Object> getValueList() {
		return valueList;
	}
	/**
	 * 获取${}中的值
	 * @param str
	 */
	public static String getConfigParent(String str){
		Pattern regex = Pattern.compile("\\#param\\{([^}]*)\\}");
		Matcher matcher = regex.matcher(str);
		while(matcher.find()) {
		    return matcher.group(1);
		}
		return null;
	}
	public static String sqlFormat(String sql){
		sql =String.format(sql,SqlParser.getTableName(),StringUtils.join(SqlParser.getColumnList(), ","),StringUtils.join(SqlParser.getValueList(), ",")); 

		return sql;
	}
	public static void main(String[] args) {
		parserInsert("INSERT INTO table_name (phone,imid,aa) VALUES (#param,?,'124');");
		System.out.println(tableName);
		System.out.println(sqlFormat("INSERT INTO %s (%s) VALUES (%s);"));
	}
}
