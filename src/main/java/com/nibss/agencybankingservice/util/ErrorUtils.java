package com.nibss.agencybankingservice.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ErrorUtils {

    public static synchronized Map<String,Object> getErrorMap(String message, String code) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp",new Date().getTime());
        map.put("message", message);
        map.put("status", code);

        return map;
    }
}
