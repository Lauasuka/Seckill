package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.seckill.utils.EncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.seckill.utils.EncryptionUtil.getMD5;

/**
 * Created by lauam on 2017/3/4.
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired // 其他J2EE标准注解 @Resource @Inject
    private SeckillDao seckillDao;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 10);
    }

    public Seckill getSeckillById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        // 缓存优化
        // 1: 先从redis中查找
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            // 缓存中没有，从数据库中查找
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {
                // 数据库中没有数据
                return new Exposer(false, seckillId);
            } else {
                // 数据库中有相关数据，保存到缓存中
                redisDao.putSeckill(seckill);
            }
        }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date currentTime = new Date();
        if (currentTime.getTime() < startTime.getTime()
                || currentTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, currentTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    @Transactional
    /**
     *  使用注解来进行事务声明的优点：
     * 1.明确标注事务方法的编程风格，可以使得开发团队达成一致
     * 2.可以保证事务方法的执行时间尽可能的短，不要穿插其他的网络操作，例如：Http请求，RPC等。
     * 如果确实需要，将相关操作剥离至方法之外。
     * 3.不是所有的方法都需要事务，比如：只有一条修改操作或者是只读操作。
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, SeckillCloseException, RepeatKillException {
        if (null == md5 || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        Date nowTime = new Date();
        // 所有数据库操作放入try中出错可以回滚
        try {
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            if (insertCount <= 0) {
                throw new RepeatKillException("seckill repeated");
            } else {
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
                if (updateCount <= 0) {
                    throw new SeckillCloseException("seckill was closed");
                } else {
                    SuccessKilled successKilled = successKilledDao.queryByIdWhitSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e) {
            throw e;
        } catch (RepeatKillException e) {
            throw e;
        } catch (SeckillException e) {
            logger.error(e.getMessage(), e);
            // 所有编译器异常，转化为运行时异常
            throw new SeckillException("seckill inner error" + e.getMessage());
        }
    }

    public SeckillExecution executeSeckillUseProcedure(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(EncryptionUtil.getMD5(seckillId))) {
            return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);

        try {
            //执行存储过程，result被赋值
            seckillDao.killByProcedure(map);
            //获取result
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                SuccessKilled sk = successKilledDao.queryByIdWhitSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, sk);
            } else {
                return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
        }
    }


}
