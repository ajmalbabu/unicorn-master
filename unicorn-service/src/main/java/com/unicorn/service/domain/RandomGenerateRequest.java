package com.unicorn.service.domain;

import java.io.Serializable;
import java.util.Map;

/**
 * Initiate a random generation request
 */
public class RandomGenerateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<String, Object> mdc;

    public RandomGenerateRequest(Map<String, Object> mdc) {
        this.mdc = mdc;
    }


    public Map<String, Object> getMdc() {
        return mdc;
    }
}
