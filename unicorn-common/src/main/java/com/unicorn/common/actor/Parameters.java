package com.unicorn.common.actor;

import akka.actor.ActorRef;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Dynamic parameters that can be set into Actor. Read java-docs in ParameterInjector.
 * This class is completely immutable and thread safe.
 */
public class Parameters {

    private static final String SENDER_ACTOR_REF = "SENDER_ACTOR_REF";

    private final Map<String, Object> parameters;


    public Parameters() {
        this(new HashMap<>());
    }

    public Parameters(Map<String, Object> parameters) {
        this.parameters = new HashMap<>(parameters);
    }


    public Parameters add(String key, Object value) {
        Map<String, Object> params = new HashMap<>(parameters);
        params.put(key, value);
        return new Parameters(params);
    }

    public Object get(String key) {
        return parameters.get(key);
    }

    public Parameters addSender(ActorRef sender) {
        return add(SENDER_ACTOR_REF, sender);
    }

    public ActorRef getSender() {
        return (ActorRef) get(SENDER_ACTOR_REF);
    }


    public Map<String, Object> getParameters() {
        return new HashMap<>(parameters);
    }

    public String getString(String param) {
        return get(param).toString();
    }

    public Integer getInteger(String param) {
        return (Integer) get(param);
    }

    public UUID getUuid(String param) {
        return (UUID) get(param);
    }

    @Override
    public String toString() {
        return "Parameters{" +
                "parameters=" + parameters +
                '}';
    }

    // Factory methods.


    public static Parameters instance() {
        return new Parameters();
    }

}
