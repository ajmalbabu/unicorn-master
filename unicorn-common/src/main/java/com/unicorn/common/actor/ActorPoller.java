package com.unicorn.common.actor;

import akka.actor.ActorRef;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;

/**
 * <p>
 * Helps to poll an actor until it finish processing a message. Or until time-out.
 * </p>
 * This itself could be converted to an actor.
 */
public class ActorPoller<T1, T2> {

    private ActorRef actorToPoll;
    private long pollSleepInterval;
    private final long timeOutMilliSecs;
    private final T1 finishCheckRequestMessage;
    private final T2 finishResponseMessage;

    public ActorPoller(ActorRef actorToPoll, long pollSleepInterval, long timeOutMilliSecs, T1 finishCheckRequestMessage, T2 finishResponseMessage) {
        this.actorToPoll = actorToPoll;
        this.pollSleepInterval = pollSleepInterval;
        this.timeOutMilliSecs = timeOutMilliSecs;
        this.finishCheckRequestMessage = finishCheckRequestMessage;
        this.finishResponseMessage = finishResponseMessage;
    }

    @SuppressWarnings("unchecked")
    public boolean poll() {

        Instant start = Instant.now();
        do {
            try {
                Future futureResult = ask(actorToPoll, finishCheckRequestMessage, timeOutMilliSecs);
                FiniteDuration duration = FiniteDuration.create(timeOutMilliSecs, TimeUnit.MILLISECONDS);
                Object result = Await.result(futureResult, duration);

                if (result.equals(finishResponseMessage)) {

                    return true;
                } else {
                    Thread.sleep(pollSleepInterval);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error polling actor: " + actorToPoll + " error: " + e, e);
            }
        } while (Duration.between(start, Instant.now()).toMillis() < timeOutMilliSecs);

        throw new RuntimeException("Timeout occurred polling actor: " + actorToPoll + " timeOutMilliSecs: " + timeOutMilliSecs);
    }


}
