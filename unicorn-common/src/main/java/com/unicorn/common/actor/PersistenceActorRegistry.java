package com.unicorn.common.actor;

import akka.actor.ActorRef;
import akka.japi.Procedure;
import akka.persistence.UntypedPersistentActor;
import com.unicorn.common.domain.PersistenceActorCreateCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import static com.unicorn.common.actor.Parameters.PERSISTENCE_ID;

/**
 * Helps to createAndRegister persistence actors so that on startup after a complete system shutdown and restart, all
 * persistence actors are started as well.
 */

@Service(PersistenceActorRegistry.PERSISTENCE_ACTOR_REGISTRY)
@Lazy
public class PersistenceActorRegistry extends UntypedPersistentActor implements ParameterInjector {
    public static final String PERSISTENCE_ACTOR_REGISTRY = "persistenceActorRegistry";

    @Autowired
    private AkkaProperties akkaProperties;

    @Autowired
    private SpringExtension springExtension;

    @Override
    public String persistenceId() {
        return PERSISTENCE_ACTOR_REGISTRY;
    }


    @Override
    public void onReceiveRecover(Object msg) throws Throwable {

        if (msg instanceof PersistenceActorCreateCommand) {

            createPersistentActor(springExtension, akkaProperties, (PersistenceActorCreateCommand) msg);

        } else {

            unhandled(msg);
        }
    }

    @Override
    public void onReceiveCommand(Object msg) throws Throwable {

        if (msg instanceof PersistenceActorCreateCommand) {

            // Command source the message without any additional work, command sourcing helps to
            // play-back commands on start-up and re-create all persistence actors.
            persist(msg, new Procedure<Object>() {

                public void apply(Object msg) throws Exception {
                }
            });
        } else {
            unhandled(msg);
        }

    }

    public static ActorRef createPersistentActor(SpringExtension springExtension, AkkaProperties akkaProperties,
                                                 PersistenceActorCreateCommand persistenceActorCreateCommand) {

        return springExtension.actorOf(akkaProperties.getActorSystem(), persistenceActorCreateCommand.getPersistenceActorSpringBeanName(),
                Parameters.instance().add(PERSISTENCE_ID, persistenceActorCreateCommand.getPersistenceActorId()), persistenceActorCreateCommand.getPersistenceActorId());

    }

    @Override
    public void setParameters(Parameters parameters) {

    }
}
