package com.unicorn.common.actor;

import akka.actor.*;
import akka.pattern.AskableActorSelection;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * To keep track of actor identity and would help to crate the actor at a later stage.
 * </p>
 */
public class ActorInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String DEFAULT_ACTOR_PATH = "user";
    public static final int RETRIEVE_ACTOR_WAIT_TIMEOUT = 2;

    private boolean isRemoteActor;

    private String actorSystemName;

    private String hostName;

    private String portNumber;

    private String actorPath;

    private String actorName;

    public ActorInfo(String actorName) {
        this(DEFAULT_ACTOR_PATH, actorName);
    }

    public ActorInfo(String actorPath, String actorName) {
        this.actorPath = actorPath;
        this.actorName = actorName;
    }

    public ActorInfo(String actorName, String actorSystemName, boolean isRemoteActor, String hostName, String portNumber) {
        this(DEFAULT_ACTOR_PATH, actorName, actorSystemName, isRemoteActor, hostName, portNumber);
    }

    public ActorInfo(String actorPath, String actorName, String actorSystemName, boolean isRemoteActor, String hostName, String portNumber) {

        this.actorPath = actorPath;
        this.actorName = actorName;
        this.actorSystemName = actorSystemName;
        this.isRemoteActor = isRemoteActor;
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    public String getActorSystemName() {
        return actorSystemName;
    }

    public void setActorSystemName(String actorSystemName) {
        this.actorSystemName = actorSystemName;
    }

    public String getHostName() {
        return hostName;
    }

    public String getPortNumber() {
        return portNumber;
    }

    public String getActorPath() {
        return actorPath;
    }

    public boolean isRemoteActor() {
        return isRemoteActor;
    }

    public void setIsRemoteActor(boolean isRemoteActor) {
        this.isRemoteActor = isRemoteActor;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

    public void setActorPath(String actorPath) {
        this.actorPath = actorPath;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getActorName() {
        return actorName;
    }

    /**
     * Retrieves the actor using the actor system and actor info in this class.
     *
     * @param actorSystem The system to retrieve the actor from.
     * @return Returns the Actor for the actor id.
     */
    public ActorRef actor(ActorSystem actorSystem) {
        ActorSelection actorSelection = actorSelection(actorSystem);

        return retrieveActor(actorSelection);
    }

    private ActorRef retrieveActor(ActorSelection actorSelection) {

        Timeout timeout = new Timeout(RETRIEVE_ACTOR_WAIT_TIMEOUT, TimeUnit.SECONDS);
        AskableActorSelection askableActorSelection = new AskableActorSelection(actorSelection);
        Future<Object> future = askableActorSelection.ask(new Identify(1), timeout);
        ActorIdentity actorIdentity;
        try {
            actorIdentity = (ActorIdentity) Await.result(future, timeout.duration());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to retrieve the requested actor.", e);
        }

        return actorIdentity.getRef();
    }

    private ActorSelection actorSelection(ActorSystem actorSystem) {

        if (isRemoteActor) {
            return actorSystem.actorSelection("akka.tcp://" + actorSystemName + "@" + hostName + ":" + portNumber + "/" + actorPath + "/" + actorName);
        }
        return actorSystem.actorSelection("/" + actorPath + "/" + actorName);

    }

    public Identifier identifier() {
        return new Identifier(getActorName());
    }

    @Override
    public String toString() {
        return "ActorInfo{" +
                "isRemoteActor=" + isRemoteActor +
                ", actorSystemName='" + actorSystemName + '\'' +
                ", hostName='" + hostName + '\'' +
                ", portNumber='" + portNumber + '\'' +
                ", actorPath='" + actorPath + '\'' +
                ", actorName='" + actorName + '\'' +
                '}';
    }
}
