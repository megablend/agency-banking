/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nibss.agencybankingservice.exceptions.CustomAmountException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author cmegafu
 */
@Slf4j
public class Util {
    
    public static Date formatDate(String dateFormat, String dateStr) throws ParseException {
        try {
            return new SimpleDateFormat(dateFormat, Locale.ENGLISH).parse(dateStr);
        } catch (ParseException e) {
            throw new ParseException(dateStr, 0);
        }
    }
    
    public static <T> String convertObjectToJson(T object) {
        try {
            ObjectMapper objMapper = new ObjectMapper();
            return objMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Unable to convert object to a string", e);
        }
        return null;
    }
    
    public static double round(double value, int places) {
        log.trace("The double value is {}", value);
        if (places < 0) throw new IllegalArgumentException();
        if (Double.isNaN(value))
            value = 0.0;
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    public static String convertDateToString(Date date, String format) {
        return new SimpleDateFormat(format, Locale.ENGLISH).format(date);
    }
    
    public static <T> T convertStringToObject(String data, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            T obj = mapper.readValue(data, clazz);
            return obj;
        } catch (IOException e) {
            log.error("Unable to parse json string {}", data, e);
        }
        return null;
    }
    
    public static long getDaysLeft(Date startDate, Date endDate) {
        return DAYS.between(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }
    
    public static <T> String convertArrayToString(List<String> data) {
        return StringUtils.join(data, ",");
    }
    
    public static BigDecimal formatAmount(String amount) throws CustomAmountException {
        try {
            return null == amount || amount.trim().isEmpty() ? BigDecimal.ZERO : new BigDecimal(amount);
        } catch (Exception e) {
            throw new CustomAmountException(amount, e.getMessage());
        }
    }
    
    public static Long formatValue(String value) {
        log.trace("The value is {}", value);
        return null != value && !value.trim().isEmpty() ? Long.valueOf(value) : null;
    }
    
    public static String generateString(int length, boolean useLetters, boolean useNumbers) {
        return RandomStringUtils.random(length, useLetters, useNumbers);
    }
    
    
}
