/**
 * 
 */
package com.chenhj.job;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chenhj.config.Config;

import org.quartz.CronScheduleBuilder;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: TimerTask.java
* @Description: 拉取条数定时器
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年10月31日 下午5:55:14 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年10月31日     chenhj          v1.0.0               修改原因
*/
public class TimerJob implements Job{
	private static final Logger LOG = LoggerFactory.getLogger(TimerJob.class);
    @Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
    		LOG.info("时间到凌晨零点,拉取条数重新置为0。。。。。");
    }
    //创建调度器
    public static Scheduler getScheduler() throws SchedulerException{
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        return schedulerFactory.getScheduler();
    }
    public static void schedulerJob() throws SchedulerException{
        //创建任务
        JobDetail jobDetail = JobBuilder.newJob(TimerJob.class).withIdentity("job1", "group1").build();
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1").withSchedule(CronScheduleBuilder.cronSchedule(Config.QUARTZ_CONFIG.getSchedule())).build(); 
        //创建触发器 每3秒钟执行一次
       // Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group3")
         //                   .withSchedule(SimpleScheduleBuilder.simpleSchedule().co.repeatForever())
         //                   .build();
        Scheduler scheduler = getScheduler();
        //将任务及其触发器放入调度器
        scheduler.scheduleJob(jobDetail, trigger);
        //调度器开始调度任务
        scheduler.start();
        
    }
}