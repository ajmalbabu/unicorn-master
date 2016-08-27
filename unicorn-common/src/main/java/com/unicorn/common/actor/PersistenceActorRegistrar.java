package com.unicorn.common.actor;

import akka.actor.ActorRef;
import com.unicorn.common.domain.PersistenceActorCreateCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.unicorn.common.actor.PersistenceActorRegistry.PERSISTENCE_ACTOR_REGISTRY;
import static com.unicorn.common.actor.PersistenceActorRegistry.createPersistentActor;

/**
 * Helps to createAndRegister persistence actors with the registry so that is it available during system restart.
 */

@Service
public class PersistenceActorRegistrar {

    @Autowired
    private AkkaProperties akkaProperties;

    @Autowired
    private SpringExtension springExtension;


    /**
     * Create persistence actor registry itself, so that other persistence actors can be registered with this registry.
     */
    private ActorRef persistenceActorRegistry;


    @PostConstruct
    public void postConstruct() {

        persistenceActorRegistry = createPersistentActor(springExtension, akkaProperties,
                new PersistenceActorCreateCommand(PERSISTENCE_ACTOR_REGISTRY, PERSISTENCE_ACTOR_REGISTRY));

    }

    /**
     * Create and and registers the persistence actor.
     *
     * @param persistenceActorCreateCommand - Information about persistence actor to be created.
     * @return Created persistence actor.
     */
    public ActorRef createAndRegister(PersistenceActorCreateCommand persistenceActorCreateCommand) {
        ActorRef actorRef = createPersistentActor(springExtension, akkaProperties, persistenceActorCreateCommand);
        persistenceActorRegistry.tell(persistenceActorCreateCommand, ActorRef.noSender());
        return actorRef;
    }


}
