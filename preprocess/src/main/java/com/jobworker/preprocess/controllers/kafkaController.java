package com.jobworker.preprocess.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class kafkaController {
    @GetMapping("/")
    public Map<String, Object> greetings(){
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("results", "Done");
        return map;
    }
}
