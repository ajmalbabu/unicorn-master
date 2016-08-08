package com.unicorn.service.domain;

import java.util.Map;


public class RandomGenerateResponse {

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
