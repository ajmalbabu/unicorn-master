package com.unicorn.service.domain;

import com.unicorn.common.domain.ServiceResponseCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class RandomGenerateResponseList implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<RandomGenerateResponse> randomResults = new ArrayList<>();
    private String advisoryMessage;
    private ServiceResponseCode serviceResponseCode = ServiceResponseCode.OK;

    public RandomGenerateResponseList() {
    }

    public RandomGenerateResponseList(ServiceResponseCode serviceResponseCode, String advisoryMessage) {
        this.serviceResponseCode = serviceResponseCode;
        this.advisoryMessage = advisoryMessage;
    }

    public RandomGenerateResponseList(List<RandomGenerateResponse> randomResults) {
        this.randomResults = randomResults;
    }

    public List<RandomGenerateResponse> getRandomResults() {
        return new ArrayList<>(randomResults);
    }

    public void setRandomResults(List<RandomGenerateResponse> randomResults) {
        this.randomResults = randomResults;
    }

    public void addRandomResult(RandomGenerateResponse randomResult) {
        this.randomResults.add(randomResult);
    }

    public String getAdvisoryMessage() {
        return advisoryMessage;
    }

    public ServiceResponseCode getServiceResponseCode() {
        return serviceResponseCode;
    }

    @Override
    public String toString() {
        return "RandomGenerateResponseList{" +
                "randomResults=" + randomResults +
                ", advisoryMessage='" + advisoryMessage + '\'' +
                ", serviceResponseCode=" + serviceResponseCode +
                '}';
    }
}
