package com.example.mybatisdemo.service.impl;


import com.example.mybatisdemo.service.TimeWheelService;
import com.example.mybatisdemo.threadpool.TimingThreadPool;
import com.example.mybatisdemo.timeWheel.TickThread;
import com.example.mybatisdemo.timeWheel.TimerTask;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author sunxu
 */
@Service
public class TimeWheelServiceImpl implements TimeWheelService {

    private final TickThread tickThread;

    private final TimingThreadPool delayTaskExecutor;

    public TimeWheelServiceImpl(TimingThreadPool delayTaskExecutor) {
        this.delayTaskExecutor = delayTaskExecutor;
        this.tickThread = new TickThread(delayTaskExecutor);
        this.tickThread.start();
    }

    @PostConstruct
    public void init() {

    }

    @Override
    public void add(TimerTask timerTask) {
        this.tickThread.addTask(timerTask);
    }

    @Override
    public void stopRuning() {
        this.tickThread.makeStop();
    }

    @Override
    public void shutdown() {
        this.tickThread.shutdown();
    }

}
