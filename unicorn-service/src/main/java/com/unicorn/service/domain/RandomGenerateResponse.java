package com.unicorn.service.domain;

import java.io.Serializable;
import java.util.Map;


public class RandomGenerateResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, Object> mdc;
    private int value;

    public RandomGenerateResponse() {
    }

    public RandomGenerateResponse(Map<String, Object> mdc, int value) {
        this.mdc = mdc;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Map<String, Object> getMdc() {
        return mdc;
    }

    @Override
    public String toString() {
        return "RandomResult{" +
                ", value=" + value +
                '}';
    }
}
