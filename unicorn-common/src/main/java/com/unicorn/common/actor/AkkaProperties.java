package com.unicorn.common.actor;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Abstracts Application specific AKKA configuration.
 */

@Component("akkaConfig")
@ConfigurationProperties(prefix = "akka", ignoreUnknownFields = true)
public class AkkaProperties {

    public static final String AKKA_REMOTE_NETTY_TCP_PORT = "akka.remote.netty.tcp.port";
    public static final String AKKA_REMOTE_NETTY_TCP_HOSTNAME = "akka.remote.netty.tcp.hostname";

    private static final Logger LOGGER = LoggerFactory.getLogger(AkkaProperties.class);

    @Value("${actor.system.name:UnicornActorSystem}")
    private String unicornActorSystem;

    @Value("${config:unicorn.akka.conf}")
    private String unicornAkkaConf;

    @Value("${actor.random.service.remote.enable:false}")
    private boolean randomServiceActorRemoteEnable;

    @Value("${actor.random.service.remote.bind.port:8995}")
    private String randomServiceActorRemoteBindPort;

    @Value("${actor.random.service.remote.bind.port:localhost}")
    private String randomServiceActorRemoteBindHost;

    @Autowired
    private SpringExtension springExtension;

    @Autowired
    private ApplicationContext applicationContext;

    private ActorSystem actorSystem;


    @PostConstruct
    public void createActorSystem() {
        LOGGER.debug("\n\n" + unicornActorSystem + randomServiceActorRemoteEnable + randomServiceActorRemoteBindHost + randomServiceActorRemoteBindPort);
        actorSystem = ActorSystem.create(unicornActorSystem, createConfig());
        springExtension.get(actorSystem).initialize(applicationContext);
        LOGGER.debug("Created actor system: {}", actorSystem);
    }


    private Config createConfig() {
        Config config = ConfigFactory.empty();

        if (isRandomServiceActorRemoteEnable()) {

            LOGGER.info("Enable remote actor is ON Create Actor system with host: {}", getRandomServiceActorRemoteBindHost());

            config = config.withFallback(ConfigFactory.parseString(AKKA_REMOTE_NETTY_TCP_HOSTNAME + " = " + getRandomServiceActorRemoteBindHost()));

            LOGGER.info("Create remote Actor system with remote port: {}", getRandomServiceActorRemoteBindPort());

            config = config.withFallback(ConfigFactory.parseString(AKKA_REMOTE_NETTY_TCP_PORT + " = " + getRandomServiceActorRemoteBindPort()))
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

    public boolean isRandomServiceActorRemoteEnable() {
        return randomServiceActorRemoteEnable;
    }

    public String getRandomServiceActorRemoteBindHost() {
        return randomServiceActorRemoteBindHost;
    }

    public String getRandomServiceActorRemoteBindPort() {
        return randomServiceActorRemoteBindPort;
    }

    public ActorSystem getActorSystem() {
        return actorSystem;
    }
}
