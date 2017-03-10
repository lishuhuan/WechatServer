package com.demo.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** 
 * @author zhuolin(zl@nbicc.com) 
 * @date 2014骞�12鏈�24鏃�
 * 绫昏鏄� 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonPolicyDef {

    Policy[] value();

    public static enum Policy {
    	APP_LOGIN,
        UNBIND_LIST,
        FIRMWARE_VERSION_LIST,
        BINDED_LIST,
        BINDED_LIST_WEB,
        QUERY_PARA,
        ENTERPRISE_DEVICE
    }
}

