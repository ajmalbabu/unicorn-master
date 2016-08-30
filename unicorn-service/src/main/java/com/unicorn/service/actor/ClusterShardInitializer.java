package com.unicorn.service.actor;

import akka.actor.Props;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.cluster.sharding.ShardRegion;
import com.unicorn.common.actor.AkkaProperties;
import com.unicorn.service.domain.ClusterShardEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.unicorn.service.actor.BankAccountPersistenceActor.BANK_ACCOUNT_PERSISTENCE_ACTOR;

@Service
public class ClusterShardInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterShardInitializer.class);

    @Autowired
    private AkkaProperties akkaProperties;

    @Value("${server.number.of.shards:10}")
    private int numberOfShards;


    @PostConstruct
    public void postConstruct() {

        LOGGER.info("Cluster shard initializer starting...");

        ClusterShardingSettings settings = ClusterShardingSettings.create(akkaProperties.getActorSystem());

        ShardRegion.MessageExtractor messageExtractor = new ShardRegion.MessageExtractor() {

            @Override
            public String entityId(Object message) {
                if (message instanceof ClusterShardEnvelope)
                    return String.valueOf(((ClusterShardEnvelope) message).id);
                else
                    return null;
            }

            @Override
            public Object entityMessage(Object message) {
                if (message instanceof ClusterShardEnvelope)
                    return ((ClusterShardEnvelope) message).payload;
                else
                    return message;
            }

            @Override
            public String shardId(Object message) {

                if (message instanceof ClusterShardEnvelope) {
                    return String.valueOf(Math.abs(((ClusterShardEnvelope) message).id.hashCode()) % numberOfShards);
                } else {
                    return null;
                }
            }
        };

        ClusterSharding.get(akkaProperties.getActorSystem()).start(BANK_ACCOUNT_PERSISTENCE_ACTOR,
                Props.create(BankAccountPersistenceActor.class),
                settings, messageExtractor);
    }
}
