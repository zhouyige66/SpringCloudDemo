package cn.roy.springcloud.api.service.impl;

import cn.roy.springcloud.api.dao.mapper.ProcessorMapper;
import cn.roy.springcloud.api.dao.bean.Processor;
import cn.roy.springcloud.api.service.ProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019-08-08 15:55
 * @Version: v1.0
 */
@Service
public class ProcessorServiceImpl implements ProcessorService {

    @Autowired
    private ProcessorMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public List<Processor> compare() {
        List<Processor> p1 = mapper.selectAssigneeList("CN507229");

        return p1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int compareAndSwap() {
        List<Processor> p1 = mapper.selectAssigneeByWorkRequestId((long) 508972);
        List<Processor> p2 = mapper.selectAssigneeList("CN507229");

        List<Long> id1 = p1.stream().map(Processor::getId).collect(Collectors.toList());
        List<Long> id2 = p2.stream().map(Processor::getId).collect(Collectors.toList());
        List<Long> id = id1.stream().filter(id2::contains).collect(Collectors.toList());

        List<Processor> p = p1.stream().filter(e -> id.contains(e.getId())).collect(Collectors.toList());
        for (Processor i : p) {
            i.setMobile("123");
            mapper.updateByPrimaryKeySelective(i);
        }

        return p.size();
    }

}
