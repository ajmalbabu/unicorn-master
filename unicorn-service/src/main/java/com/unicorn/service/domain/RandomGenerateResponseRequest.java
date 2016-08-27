package com.unicorn.service.domain;

import java.io.Serializable;
import java.util.Map;

/**
 * Request for the random generated response list.
 */
public class RandomGenerateResponseRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private Map<String, Object> mdc;

    public RandomGenerateResponseRequest() {
    }

    public RandomGenerateResponseRequest(Map<String, Object> mdc) {
        this.mdc = mdc;
    }

    public Map<String, Object> getMdc() {
        return mdc;
    }
}
