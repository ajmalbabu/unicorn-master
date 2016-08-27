package com.unicorn.api;

import com.unicorn.common.actor.ActorInfo;
import com.unicorn.common.actor.Identifier;
import com.unicorn.common.service.CacheService;
import com.unicorn.service.RandomService;
import com.unicorn.service.domain.RandomGenerateResponseList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Show-case how to implement Fan Out using AKKA.
 * 1. API calls to create a fan out process.
 * 2. And another API can collect the results.
 */
@RestController
@RequestMapping("/v1")
public class AkkaFanOutShowcaseRestApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(AkkaFanOutShowcaseRestApi.class);

    @Autowired
    private RandomService randomService;

    @Autowired
    private CacheService cacheService;


    @RequestMapping(value = "fan-out-random-generate", method = RequestMethod.GET)
    public ResponseEntity<Identifier> fanOutRandomGenerate(@RequestParam(name = "count") Integer count) {

        LOGGER.info("Started random generate method.");

        ActorInfo actorInfo = randomService.randomGenerate(count);

        cacheService.add(actorInfo.getActorName(), actorInfo);

        LOGGER.info("Completed random generate method.");

        return new ResponseEntity<>(actorInfo.identifier(), HttpStatus.OK);

    }

    @RequestMapping(value = "fan-out-random-generate-response", method = RequestMethod.GET)
    public ResponseEntity<RandomGenerateResponseList> fanOutRandomGenerateResponse(@RequestParam(name = "id") String id) {

        LOGGER.info("Started random generate response method.");

        ActorInfo actorInfo = cacheService.get(id);

        RandomGenerateResponseList randomGenerateResponseList = randomService.randomGenerateResponse(actorInfo);

        LOGGER.info("Completed random generate response method.");

        return new ResponseEntity<>(randomGenerateResponseList, HttpStatus.OK);

    }


}
