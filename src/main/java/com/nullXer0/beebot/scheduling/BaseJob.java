package com.nullXer0.beebot.scheduling;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Trigger;

public abstract class BaseJob implements Job
{
    public abstract JobDetail getJobDetail();

    public abstract Trigger getTrigger();
}
