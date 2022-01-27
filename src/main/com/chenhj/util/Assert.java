package com.chenhj.util;

import org.apache.commons.lang3.StringUtils;

public abstract class Assert {

    public Assert() {
    }

    public static void requireNonNull(Object obj,String msg){
        if(obj==null){
            throw new NullPointerException(msg);
        }
        if(obj instanceof String){
            String mss = String.valueOf(obj);
            if(StringUtils.isBlank(mss)){
                throw new NullPointerException(msg);
            }
        }
    }
}
