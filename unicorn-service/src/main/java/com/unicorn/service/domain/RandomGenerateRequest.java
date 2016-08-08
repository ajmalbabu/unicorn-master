package com.unicorn.service.domain;

import java.util.Map;

/**
 * Initiate a random generation request
 */
public class RandomGenerateRequest {

    private final Map<String, Object> mdc;

    public RandomGenerateRequest(Map<String, Object> mdc) {
        this.mdc = mdc;
    }


    public Map<String, Object> getMdc() {
        return mdc;
    }
}
