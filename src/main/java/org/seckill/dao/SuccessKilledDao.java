package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;
import org.springframework.stereotype.Repository;

/**
 * Created by lauam on 2017/3/3.
 */
@Repository
public interface SuccessKilledDao {

    /**
     * 插入购买明细，可过滤重复（联合主键过滤）
     * @param seckillId
     * @param userPhone
     * @return
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     * 根据id查询successKilled并携带秒杀产品对象实体
     * @param seckillId
     * @param userPhone
     * @return
     */
    SuccessKilled queryByIdWhitSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

}
