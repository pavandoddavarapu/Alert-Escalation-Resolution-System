package com.moveinsync.alertsystem.rules;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;

@Component
public class RuleEngine {

    private Map<String, Map<String, Integer>> rules;

    public RuleEngine() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getClassLoader().getResourceAsStream("rules.json");
            rules = mapper.readValue(is, Map.class);
            System.out.println("Rules loaded: " + rules);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer getEscalationMinutes(String type){
        if(rules.containsKey(type)){
            return rules.get(type).get("escalate_minutes");
        }
        return null;
    }

    public Integer getAutoCloseMinutes(String type){
        if(rules.containsKey(type)){
            return rules.get(type).get("auto_close_minutes");
        }
        return null;
    }
}