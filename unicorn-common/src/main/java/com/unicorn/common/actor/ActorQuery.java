package com.unicorn.common.actor;

import akka.actor.ActorRef;
import org.springframework.stereotype.Service;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;

/**
 * <p>
 * A convenient class that can query actor for response in synchronous style with timeout.
 * </p>
 */
@Service
public class ActorQuery {

    private static final long DEFAULT_TIME_OUT_MILLIES = 3000;

    @SuppressWarnings("unchecked")
    public <T> T query(ActorRef actorRef, Object request) {

        try {

            Future futureResult = ask(actorRef, request, DEFAULT_TIME_OUT_MILLIES);
            FiniteDuration duration = FiniteDuration.create(DEFAULT_TIME_OUT_MILLIES, TimeUnit.MILLISECONDS);

            return (T) Await.result(futureResult, duration);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
