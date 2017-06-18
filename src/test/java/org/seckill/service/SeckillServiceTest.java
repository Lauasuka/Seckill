package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.utils.EncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by lauam on 2017/3/4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
                       "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> seckills = seckillService.getSeckillList();
        logger.info("seckills={}", seckills);
    }

    @Test
    public void getSeckillById() throws Exception {
        long seckillId = 1000L;
        Seckill seckill = seckillService.getSeckillById(seckillId);
        logger.info("seckill={}", seckill);
    }

    // 秒杀业务完整流程测试
    @Test
    public void sekillLogic() throws Exception{
        long seckillId = 1001L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()){
            logger.info("exposer={}", exposer);
            String md5 = exposer.getMd5();
            long userPhone = 15820591100L;
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, userPhone, md5);
                logger.info("seckillExecution  = {}", seckillExecution);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            } catch (SeckillCloseException e){
                logger.error(e.getMessage());
            }
        } else {
            logger.warn("exposer={}", exposer);
        }
    }

    @Test
    public void executeSeckillProcedure(){
        long seckillId = 1003L;
        long phone = 15820591177L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()) {
            String md5 = EncryptionUtil.getMD5(seckillId);
            SeckillExecution execution = seckillService.executeSeckillUseProcedure(seckillId, phone, md5);
            logger.info("execution={}", execution.getStateInfo());
        }
    }

//    @Test
//    public void exportSeckillUrl() throws Exception {
//        long seckillId = 1001L;
//        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
//        logger.info("exposer={}", exposer);
//    }
//
//    @Test
//    public void executeSeckill() throws Exception {
//        long seckillId = 1001L;
//        long userPhone = 15820591188L;
//        String md5 = "b0416ac14dd942ebc42402d23b9557e8";
//        SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, userPhone, md5);
//        logger.info("seckillExecution  = {}", seckillExecution);
//    }

}