package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * Created by lauam on 2017/3/4.
 * @Description 业务接口
 * 业务接口的设计原则：要站在“使用者”的角度设计接口，具体涉及以下三个方面：
 * 方法定义的粒度，参数，返回类型（return 类型/抛出异常）
 */
public interface SeckillService {

    /**
     * 查询所有秒杀商品记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 获取单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getSeckillById(long seckillId);

    /**
     * 秒杀开始的时候输出秒杀接口地址，
     * 否则输出秒杀时间和系统当前的时间
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws SeckillCloseException
     * @throws RepeatKillException
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, SeckillCloseException, RepeatKillException;

    SeckillExecution executeSeckillUseProcedure(long seckillId, long userPhone, String md5);
}
