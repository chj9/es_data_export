/**
 * 
 */
package com.chenhj.init;

import java.io.File;
import java.io.FileNotFoundException;

import org.slf4j.LoggerFactory;

import com.chenhj.constant.Constant;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: LogBack.java
* @Description: 日志初始化
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月7日 下午2:40:12 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月7日     chenhj          v1.0.0               修改原因
*/
public class InitLogBack {
	
	public static void init() throws Exception{
 		//String configFilepathName = FilePathHelper.getFilePathWithJar("logback.xml");
        File file = new File(Constant.LOGBACK_CONFIG_NAME);
        if(!file.exists()){
        	throw new FileNotFoundException("logback.xml 不存在");
        }
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        //注意包不要引错
        JoranConfigurator joranConfigurator = new JoranConfigurator();
        joranConfigurator.setContext(loggerContext);
        loggerContext.reset();
        try {
            joranConfigurator.doConfigure(file);
        } catch (Exception e) {
        	throw e;
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
	}
}
