package com.moveinsync.alertsystem.rules;

import java.util.Map;

public class RuleConfig {

    private Map<String, Map<String, Integer>> rules;

    public Map<String, Map<String, Integer>> getRules() {
        return rules;
    }

    public void setRules(Map<String, Map<String, Integer>> rules) {
        this.rules = rules;
    }
}