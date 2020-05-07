package com.xuecheng.order.service;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    XcTaskRepository xcTaskRepository;
    @Autowired
    XcTaskHisRepository xcTaskHisRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    //查询前n条任务
    public List<XcTask> findTaskList(Date updateTime, int n) {
        //设置分页参数
        Pageable pageable = PageRequest.of(0, n);
        //查询前n条记录
        Page<XcTask> xcTasks = xcTaskRepository.findByUpdateTimeBefore(pageable, updateTime);
        return xcTasks.getContent();

    }

    /**
     * //发送消息
     *
     * @param xcTask     任务对象
     * @param ex         交换机id
     * @param routingKey
     */
    @Transactional
    public void publish(XcTask xcTask, String ex, String routingKey) {
        //查询任务
        Optional<XcTask> taskOptional = xcTaskRepository.findById(xcTask.getId());
        if (taskOptional.isPresent()) {
            XcTask one = taskOptional.get();
            //String exchange, String routingKey, Object object(消息体)
            rabbitTemplate.convertAndSend(ex, routingKey, one);
            //更新任务时间为当前时间
            one.setUpdateTime(new Date());
            xcTaskRepository.save(one);
        }
    }
    //获取任务
    @Transactional
    public int getTask(String taskId,int version){
        //通过乐观锁方式更新数据库，如果>0则取到任务
        int i = xcTaskRepository.updateTaskVersion(taskId, version);
        return i;
    }
    //删除任务
    @Transactional
    public void finishTask(String taskId){
        Optional<XcTask> taskOptional = xcTaskRepository.findById(taskId);
        if(taskOptional.isPresent()){
            XcTask xcTask = taskOptional.get();
            xcTask.setDeleteTime(new Date());
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask, xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
            xcTaskRepository.delete(xcTask);
        }
    }
}
