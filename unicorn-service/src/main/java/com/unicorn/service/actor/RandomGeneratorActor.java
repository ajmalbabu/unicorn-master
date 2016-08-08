package com.unicorn.service.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.DiagnosticLoggingAdapter;
import akka.event.Logging;
import com.unicorn.service.domain.RandomGenerateRequest;
import com.unicorn.service.domain.RandomGenerateResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Random;


/**
 * <p>
 * This actor collects the results. This actor will terminate after a period of 30 minutes of
 * inactivity. Inactivity starts from the last time this actor received a message.
 */
@Service(RandomGeneratorActor.RANDOM_GENERATOR_ACTOR)
@Scope("prototype")
@Lazy
public class RandomGeneratorActor extends UntypedActor {

    public static final String RANDOM_GENERATOR_ACTOR = "randomGeneratorActor";
    public static final String RANDOM_GENERATOR_ACTOR_DISPATCHER = "task-runner-actor-dispatcher";

    private DiagnosticLoggingAdapter log = Logging.getLogger(this);


    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof RandomGenerateRequest) {
            RandomGenerateRequest request = (RandomGenerateRequest) message;

            log.setMDC(request.getMdc());
            log.info("Generating Random number");

            sender().tell(new RandomGenerateResponse(((RandomGenerateRequest) message).getMdc(), new Random().nextInt()), ActorRef.noSender());

        } else {
            log.error("unknown message to process: {}", message);
            unhandled(message);
        }
    }


}