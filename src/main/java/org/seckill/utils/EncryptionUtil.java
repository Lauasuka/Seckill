package org.seckill.utils;

import org.springframework.util.DigestUtils;

/**
 * Created by lauam on 2017/3/4.
 */
public class EncryptionUtil {

    // md5盐值字符串， 用于混淆md5
    private final static String slat = "qwuiehtp9p23ytp9824tghqugpp4*3*22";

    public static String getMD5(long seckillId){
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
}
