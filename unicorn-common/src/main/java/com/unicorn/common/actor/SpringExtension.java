package com.unicorn.common.actor;

import akka.actor.*;
import akka.routing.RouterConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * An Akka Extension to provide access to Spring managed Actor Beans.
 */
@Service
public class SpringExtension extends AbstractExtensionId<SpringExtension.SpringExt> {

    /**
     * Is used by Akka to instantiate the Extension identified by this
     * ExtensionId, internal use only.
     */
    @Override
    public SpringExt createExtension(ExtendedActorSystem system) {
        return new SpringExt();
    }

    public ActorRef actorOf(ActorContext actorContext, String actorName, Parameters actorParameters, String actorLogicalName) {
        return actorContext.actorOf(get(actorContext.system()).props(actorName, actorParameters), actorLogicalName);
    }

    public ActorRef actorOf(ActorContext actorContext, String actorName, Parameters actorParameters) {
        return actorContext.actorOf(get(actorContext.system()).props(actorName, actorParameters));
    }

    public ActorRef actorOf(ActorSystem actorSystem, String actorName, Parameters actorParameters) {
        return actorSystem.actorOf(get(actorSystem).props(actorName, actorParameters));
    }

    public ActorRef actorOf(ActorSystem actorSystem, String actorName) {
        return actorSystem.actorOf(get(actorSystem).props(actorName));
    }

    public ActorRef actorOf(ActorSystem actorSystem, String actorName, RouterConfig routerConfig, String dispatcher, String actorLogicalName) {
        return actorSystem.actorOf(get(actorSystem).props(actorName).withRouter(routerConfig).withDispatcher(dispatcher), actorLogicalName);

    }

    public ActorRef actorOf(ActorSystem actorSystem, String actorName, RouterConfig routerConfig, String dispatcher, Parameters actorParameters, String actorLogicalName) {
        return actorSystem.actorOf(get(actorSystem).props(actorName, actorParameters).withRouter(routerConfig).withDispatcher(dispatcher), actorLogicalName);

    }


    public ActorRef actorOf(ActorSystem actorSystem, String actorName, Parameters actorParameters, String actorLogicalName) {
        return actorSystem.actorOf(get(actorSystem).props(actorName, actorParameters), actorLogicalName);

    }

    /**
     * The Extension implementation.
     */
    public static class SpringExt implements Extension {
        private volatile ApplicationContext applicationContext;

        /**
         * Used to initialize the Spring application context for the extension.
         *
         * @param applicationContext - spring application context.
         */
        public void initialize(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }


        /**
         * Create a Props for the specified actorBeanName using the
         * SpringActorProducer class.
         *
         * @param actorBeanName The name of the actor bean to create Props for
         * @return a Props that will create the named actor bean using Spring
         */
        public Props props(String actorBeanName) {
            return props(actorBeanName, null);
        }

        /**
         * Create a Props for the specified actorBeanName using the
         * SpringActorProducer class.
         *
         * @param actorBeanName The name of the actor bean to create Props for
         * @param parameters    If any parameters this Actor needs, pass null if no parameters.
         * @return a Props that will create the named actor bean using Spring
         */
        public Props props(String actorBeanName, Parameters parameters) {
            return Props.create(SpringActorProducer.class, applicationContext, actorBeanName, parameters);
        }


    }
}
