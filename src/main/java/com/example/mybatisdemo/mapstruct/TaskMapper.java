package com.example.mybatisdemo.mapstruct;


import com.example.mybatisdemo.disruptor.TaskQueueItem;
import com.example.mybatisdemo.pojo.CommonTask;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


/**
 * @author sunxu
 */
@Mapper
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);


    /**
     * 将DTO转换为POJO
     * @param taskQueueItem 输入参数
     * @return 返回存入数据库的pojo
     */
    CommonTask dto2Po(TaskQueueItem taskQueueItem);
}
