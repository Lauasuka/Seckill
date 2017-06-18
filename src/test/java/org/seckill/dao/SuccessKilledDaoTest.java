package org.seckill.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by lauam on 2017/3/4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() throws Exception {
        int insertCount = successKilledDao.insertSuccessKilled(1000L, 15820591188L);
        System.out.println("insertCount: " + insertCount);
    }

    @Test
    public void queryByIdWhitSeckill() throws Exception {
        SuccessKilled successKilled = successKilledDao.queryByIdWhitSeckill(1000L, 15820591188L);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }

}