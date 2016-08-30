package com.unicorn.service.domain;

import java.io.Serializable;

/**
 * Wrapper for cluster shard.
 */
public class ClusterShardEnvelope implements Serializable{

    private static final long serialVersionUID = 1L;
    final public Object id;
    final public Object payload;

    public ClusterShardEnvelope(Object id, Object payload) {
        this.id = id;
        this.payload = payload;
    }
}
