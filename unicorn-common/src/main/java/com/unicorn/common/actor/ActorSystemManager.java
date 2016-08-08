package com.unicorn.common.actor;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Abstracts AKKA configuration.
 */

@Service("actorSystemManager")
public class ActorSystemManager {

    public static final String AKKA_REMOTE_NETTY_TCP_PORT = "akka.remote.netty.tcp.port";
    public static final String AKKA_REMOTE_NETTY_TCP_HOSTNAME = "akka.remote.netty.tcp.hostname";

    private static final Logger LOGGER = LoggerFactory.getLogger(ActorSystemManager.class);


    @Value("${unicorn.engine.actor.system:UnicornActorSystem}")
    private String unicornActorSystem;

    @Value("${unicorn.engine.enable.remote.actor:false}")
    private boolean enableRemoteActor;

    @Value("${server.address:localhost}")
    private String remoteBindHostName;

    @Value("${unicorn.engine.remote.bind.port:8995}")
    private String remoteBindPort;

    @Value("${unicorn.akka.config:unicorn.akka.conf}")
    private String unicornAkkaConf;

    @Autowired
    private SpringExtension springExtension;

    @Autowired
    private ApplicationContext applicationContext;

    private ActorSystem actorSystem;


    @PostConstruct
    public void createActorSystem() {
        LOGGER.debug("\n\n" + unicornActorSystem + enableRemoteActor + remoteBindHostName + remoteBindPort);
        actorSystem = ActorSystem.create(unicornActorSystem, createConfig());
        springExtension.get(actorSystem).initialize(applicationContext);
        LOGGER.debug("Created actor system: {}", actorSystem);
    }


    private Config createConfig() {
        Config config = ConfigFactory.empty();

        if (isEnableRemoteActor()) {

            LOGGER.info("Enable remote actor is ON Create Actor system with host: {}", getRemoteBindHostName());

            config = config.withFallback(ConfigFactory.parseString(AKKA_REMOTE_NETTY_TCP_HOSTNAME + " = " + getRemoteBindHostName()));

            LOGGER.info("Create remote Actor system with remote port: {}", getRemoteBindPort());

            config = config.withFallback(ConfigFactory.parseString(AKKA_REMOTE_NETTY_TCP_PORT + " = " + getRemoteBindPort()))
                    .withFallback(ConfigFactory.parseString("akka.actor.provider = akka.remote.RemoteActorRefProvider"));
        }

        if (unicornAkkaConf.trim().length() > 0) {
            LOGGER.info("Fall back to provided unicorn.akka.conf file {}", unicornAkkaConf);
            config = config.withFallback(ConfigFactory.load(unicornAkkaConf));
        }

        String formattedConfig = config.toString().replace(",", ",\n");
        LOGGER.info("Unicorn akka config {}", formattedConfig);

        return config;
    }

    public String getUnicornActorSystem() {
        return unicornActorSystem;
    }

    public boolean isEnableRemoteActor() {
        return enableRemoteActor;
    }

    public String getRemoteBindHostName() {
        return remoteBindHostName;
    }

    public String getRemoteBindPort() {
        return remoteBindPort;
    }

    public ActorSystem getActorSystem() {
        return actorSystem;
    }
}
