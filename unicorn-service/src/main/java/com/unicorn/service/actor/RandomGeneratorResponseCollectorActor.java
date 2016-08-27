package com.unicorn.service.actor;

import akka.actor.ActorRef;
import akka.actor.ReceiveTimeout;
import akka.actor.UntypedActor;
import akka.event.DiagnosticLoggingAdapter;
import akka.event.Logging;
import com.unicorn.common.actor.ParameterInjector;
import com.unicorn.common.actor.Parameters;
import com.unicorn.common.domain.ServiceResponseCode;
import com.unicorn.common.service.TransactionIdService;
import com.unicorn.service.domain.RandomGenerateResponse;
import com.unicorn.service.domain.RandomGenerateResponseList;
import com.unicorn.service.domain.RandomGenerateResponseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;


/**
 * <p>
 * This actor collects the results. This actor will terminate after a period of 30 minutes of
 * inactivity. Inactivity starts from the last time this actor received a message.
 */
@Service(RandomGeneratorResponseCollectorActor.RANDOM_SEARCH_RESP_COLLECTOR_ACTOR)
@Scope("prototype")
@Lazy
public class RandomGeneratorResponseCollectorActor extends UntypedActor implements ParameterInjector {

    public static final String RANDOM_SEARCH_RESP_COLLECTOR_ACTOR = "randomSearchResponseCollectorActor";
    public static final String RANDOM_SEARCH_RESP_COLLECTOR_ACTOR_DISPATCHER = "task-runner-actor-dispatcher";
    public static final String EXPECTED_NUMBER_OF_MESSAGE = "expectedNumberOfMessage";

    private DiagnosticLoggingAdapter log = Logging.getLogger(this);

    private static final long ACTOR_LIFETIME = 1000 * 60 * 30;

    @Autowired
    private TransactionIdService transactionIdService;

    private int expectedNumberOfMessage;

    private int messagesReceived = 0;

    private final RandomGenerateResponseList resultCollection = new RandomGenerateResponseList();


    public RandomGeneratorResponseCollectorActor() {

        getContext().setReceiveTimeout(Duration.create(ACTOR_LIFETIME, TimeUnit.MILLISECONDS));

    }

    @Override
    public void setParameters(Parameters parameters) {
        expectedNumberOfMessage = parameters.getInteger(EXPECTED_NUMBER_OF_MESSAGE);
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof RandomGenerateResponse) {
            collectRandomGenerateResult((RandomGenerateResponse) message);
        } else if (message instanceof RandomGenerateResponseRequest) {
            handleRandomResponseRequest((RandomGenerateResponseRequest) message);

        } else if (message.toString().equals("status")) {
            checkProcessFinished();
        } else if (message instanceof ReceiveTimeout) {
            handleTimeout();

        } else {
            log.error("unknown message to process: {}", message);
            unhandled(message);
        }
    }


    private void collectRandomGenerateResult(RandomGenerateResponse result) {

        log.setMDC(result.getMdc());
        transactionIdService.setTransactionId(result.getMdc());
        try {
            this.resultCollection.addRandomResult(result);
            messagesReceived++;
            log.info("Messages count received so far: {}", messagesReceived);

        } finally {
            transactionIdService.clear();
            log.clearMDC();
        }
    }

    private void handleRandomResponseRequest(RandomGenerateResponseRequest randomResultRequest) {

        log.setMDC(randomResultRequest.getMdc());
        transactionIdService.setTransactionId(randomResultRequest.getMdc());
        try {
            if (isProcessCompleted()) {
                sender().tell(resultCollection, ActorRef.noSender());
            } else {
                String message = "Result collection is not completed, try again after sometime, current count result is: " + messagesReceived;
                sender().tell(new RandomGenerateResponseList(ServiceResponseCode.RESULT_COLLECTION_INCOMPLETE, message),
                        ActorRef.noSender());
            }
        } finally {
            transactionIdService.clear();
            log.clearMDC();

        }
    }

    private boolean isProcessCompleted() {
        return messagesReceived == this.expectedNumberOfMessage;
    }


    private void checkProcessFinished() {
        if (isProcessCompleted()) {
            log.debug("Finished!");

            sender().tell("Finished", ActorRef.noSender());
        } else {
            log.debug("Not finished!");

            sender().tell("NotFinished", ActorRef.noSender());
        }
    }

    private void handleTimeout() {
        log.debug("Actor terminating due to timeout limit...");
        getContext().stop(self());
    }

}