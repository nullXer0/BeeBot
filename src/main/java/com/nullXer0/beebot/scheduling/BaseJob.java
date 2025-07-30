package com.nullXer0.beebot.scheduling;

import org.quartz.*;

public abstract class BaseJob implements Job
{
    public abstract JobDetail getJobDetail();

    public abstract Trigger getTrigger();

    public void addToScheduler(Scheduler scheduler) throws SchedulerException
    {
        scheduler.scheduleJob(getJobDetail(), getTrigger());
    }
}
