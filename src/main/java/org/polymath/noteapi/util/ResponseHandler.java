package org.polymath.noteapi.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHandler{
    public static ResponseEntity<?> handleResponse(Object body, HttpStatus status,String message){
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("status", status);
        map.put("message", message);
        if(body!=null)map.put("body", body);
        return new ResponseEntity<>(map, status);
    }
}