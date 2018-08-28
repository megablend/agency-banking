package com.nibss.agencybankingservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilterUtils {

    private final static ObjectMapper mapper = new ObjectMapper();

    public synchronized static void writeErrorMap(Map<String,Object> errorMap, HttpServletResponse response, int httpStatus) throws IOException {
        String content = mapper.writeValueAsString(errorMap);
        response.setStatus(httpStatus);
        response.setContentType("application/json");
        response.getWriter().write(content);
    }

    public  synchronized static void doErrorResponse(HttpServletResponse httpServletResponse, String message, String code, int scUnauthorized) throws IOException {
        Map<String, Object> map = ErrorUtils.getErrorMap(message, code);
        writeErrorMap(map, httpServletResponse, scUnauthorized);
    }
    
    public synchronized static boolean isSignatureValid(final String signature, final String username, final String password, final String method) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        String hashedSignature =  new StringBuilder(username).append(date).append(password).toString();
        log.trace("hashed string {}", hashedSignature);
        String resolvedHashValue = resolveHash(method, hashedSignature);
        log.trace("rehashed string {}", resolvedHashValue);
        return  signature.trim().equals(resolvedHashValue.trim());
    }
    
    public synchronized static String showSignature(final String signature, final String username, final String password, final String method) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        String hashedSignature =  new StringBuilder(username).append(date).append(password).toString();
        log.trace("hashed string {}", hashedSignature);
        String resolvedHashValue = resolveHash(method, hashedSignature);
        log.trace("rehashed string {}", resolvedHashValue);
        return  resolvedHashValue.trim();
    }
    
    public synchronized static boolean isAllowedSignatureMethod(String method) {
        return (method != null && (method.trim().equals("SHA512") || method.trim().equals("SHA256")));
    }
    
    private synchronized  static String resolveHash(String method, String hashedString) {
        return method.trim().equals("SHA256") ? SecurityUtil.doSHA256(hashedString) : SecurityUtil.doSHA512(hashedString);
    }
    
    public synchronized static StringTokenizer getUsernameAndPassword(String token, String scheme) {
        String encodedUsernameAndPassword = token.replaceFirst(scheme + " ", "");
        log.trace("The encoded string is {}", encodedUsernameAndPassword);
        String usernameAndPassword = new String(org.apache.commons.codec.binary.Base64.decodeBase64(encodedUsernameAndPassword.getBytes()));
        log.trace("The decoded string {}", usernameAndPassword);
        return new StringTokenizer(usernameAndPassword, ":");
    }
}
