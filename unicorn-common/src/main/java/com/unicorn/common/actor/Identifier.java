package com.unicorn.common.actor;

import java.util.UUID;


public class Identifier {
    private String id;


    public Identifier() {
    }


    public Identifier(UUID id) {
        this.id = id.toString();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Identifier{" +
                "id='" + id + '\'' +
                '}';
    }
}