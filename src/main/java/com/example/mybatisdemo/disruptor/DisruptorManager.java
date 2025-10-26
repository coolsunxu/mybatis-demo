package com.example.mybatisdemo.disruptor;

import com.example.mybatisdemo.config.props.DisruptorConfig;
import com.example.mybatisdemo.service.CommonTaskService;
import com.example.mybatisdemo.threadpool.TimingThreadPool;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

import static com.example.mybatisdemo.util.MathUtil.getHashCode;

/**
 * @author sunxu
 */

@Component
@Slf4j
public class DisruptorManager {


    private final DisruptorConfig disruptorConfig;

    private final TimingThreadPool fastTaskExecutor;

    private final TimingThreadPool slowTaskExecutor;

    private final CommonTaskService commonTaskService;

    private final ArrayList<Disruptor<TaskQueueItem>> disruptorArrayList = new ArrayList<>();

    public DisruptorManager(
            DisruptorConfig disruptorConfig,
            TimingThreadPool fastTaskExecutor,
            TimingThreadPool slowTaskExecutor,
            CommonTaskService commonTaskService
    ) {
        this.disruptorConfig = disruptorConfig;
        this.fastTaskExecutor = fastTaskExecutor;
        this.slowTaskExecutor = slowTaskExecutor;
        this.commonTaskService = commonTaskService;
    }


    @PostConstruct
    public void start() {
        for (int i = 0; i < disruptorConfig.getQueueSize(); i++) {

            int finalI = i;
            Disruptor<TaskQueueItem> disruptor = new Disruptor<>(
                    TaskQueueItem::new,
                    disruptorConfig.getBufferSize(),
                    r -> {
                        return new Thread(r, "consume" + finalI);
                    },
                    ProducerType.SINGLE,
                    new BlockingWaitStrategy());

            // 设置EventHandler
            disruptor.handleEventsWith(new TaskHandler(fastTaskExecutor, slowTaskExecutor, commonTaskService));

            disruptorArrayList.add(disruptor);

            disruptorArrayList.get(i).start();
        }
    }

    public void addTask(TaskQueueItem taskQueueItem) {
        int index = getHashCode(taskQueueItem.getDeviceId()) & (disruptorConfig.getQueueSize() - 1);



        RingBuffer<TaskQueueItem> ringBuffer = disruptorArrayList.get(index).getRingBuffer();
        // 获取下一个可用位置的下标
        long sequence = ringBuffer.next();
        try {
            // 返回可用位置的元素
            TaskQueueItem event = ringBuffer.get(sequence);
            event.setCf(taskQueueItem.getCf());
            event.setContent(taskQueueItem.getContent());
            event.setDeviceId(taskQueueItem.getDeviceId());
            event.setCreateTime(taskQueueItem.getCreateTime());
            event.setBusId(taskQueueItem.getBusId());
            event.setBizType(taskQueueItem.getBizType());
            event.setExpiredTime(taskQueueItem.getExpiredTime());
        } finally {

            ringBuffer.publish(sequence);
        }
    }
}
