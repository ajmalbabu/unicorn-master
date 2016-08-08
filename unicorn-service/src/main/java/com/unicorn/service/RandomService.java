package com.unicorn.service;

import akka.actor.ActorRef;
import akka.routing.FromConfig;
import com.unicorn.common.actor.*;
import com.unicorn.common.service.TransactionIdService;
import com.unicorn.service.actor.RandomGeneratorActor;
import com.unicorn.service.actor.RandomGeneratorResponseCollectorActor;
import com.unicorn.service.domain.RandomGenerateRequest;
import com.unicorn.service.domain.RandomGenerateResponseList;
import com.unicorn.service.domain.RandomGenerateResponseRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.UUID;

/**
 * Created by e120768 on 7/29/2016.
 */
@Service
public class RandomService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RandomService.class);

    @Autowired
    private ActorSystemManager actorSystemManager;

    @Autowired
    private TransactionIdService transactionIdService;

    @Autowired
    private SpringExtension springExtension;

    @Autowired
    private ActorQuery actorQuery;


    private ActorRef randomGeneratorActor;

    @PostConstruct
    public void postConstruct() {

        randomGeneratorActor = springExtension.actorOf(actorSystemManager.getActorSystem(), RandomGeneratorActor.RANDOM_GENERATOR_ACTOR,
                new FromConfig(), RandomGeneratorActor.RANDOM_GENERATOR_ACTOR_DISPATCHER, RandomGeneratorActor.RANDOM_GENERATOR_ACTOR);
    }

    /**
     * Creates <code>count</code> number of actors and ask them to generate random numbers and ask them to send
     * to the coordinator actor for results.
     *
     * @param count - number of random numbers to be generated.
     * @return The identifier of the collector actor to whom it can be queried later for the complete results.
     */
    public ActorInfo randomGenerate(int count) {

        LOGGER.trace("Started random generators.");

        Map<String, Object> transactionId = transactionIdService.currentTransactionIdAsMap();

        UUID collectorActorName = UUID.randomUUID();

        // Create actor so that it is a spring bean as well, pass the dispatcher name that need to match with config
        // file and also pass necessary parameters needed for the actor, name of the actor as the uuid string which
        // can be looked up later in another transaction.
        ActorRef randomSearchResponseCollectorActor = springExtension.actorOf(actorSystemManager.getActorSystem(),
                RandomGeneratorResponseCollectorActor.RANDOM_SEARCH_RESP_COLLECTOR_ACTOR,
                Parameters.instance().add(RandomGeneratorResponseCollectorActor.EXPECTED_NUMBER_OF_MESSAGE, count), collectorActorName.toString());

        LOGGER.info("Starting Random generation for transaction {} with collector {} - # of random generator actors {}.",
                transactionId, collectorActorName, count);

        for (int i = 0; i < count; i++) {
            randomGeneratorActor.tell(new RandomGenerateRequest(transactionId), randomSearchResponseCollectorActor);
        }

        LOGGER.trace("Initiated random generator for transaction {} with collector: {}", transactionId, collectorActorName);

        return new ActorInfo(actorSystemManager.getUnicornActorSystem(), actorSystemManager.isEnableRemoteActor(),
                actorSystemManager.getRemoteBindHostName(), actorSystemManager.getRemoteBindPort(), collectorActorName);

    }

    public RandomGenerateResponseList randomGenerateResponse(ActorInfo actorInfo) {

        RandomGenerateResponseRequest randomGenerateResponseRequest = new RandomGenerateResponseRequest(transactionIdService.currentTransactionIdAsMap());

        ActorRef actorRef = actorInfo.actor(actorSystemManager.getActorSystem());

        return actorQuery.query(actorRef, randomGenerateResponseRequest);
    }


}
