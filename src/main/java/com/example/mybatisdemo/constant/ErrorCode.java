package com.example.mybatisdemo.constant;

/**
 * @author sunxu
 */
public class ErrorCode {

    public final static int SUCCESS = 0;

    public final static int UNKNOWN_ERROR = -20000;
    public final static int SAVE_INTO_DB_ERROR = -20001;


    /**
     * 采用disruptor 环形数组 出现队列满的情况很低
     */
    public final static int QUEUE_FULL = -20002;

    public final static int TIME_OUT = -20003;

    public final static int TASK_EXPIRED = -20004;

    public final static int THREAD_POOL_REJECT = -20005;


}
