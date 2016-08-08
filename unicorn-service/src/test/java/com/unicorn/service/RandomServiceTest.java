package com.unicorn.service;

import com.unicorn.common.actor.ActorInfo;
import com.unicorn.common.actor.Identifier;
import com.unicorn.common.domain.ServiceResponseCode;
import com.unicorn.service.domain.RandomGenerateResponseList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by e120768 on 8/8/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringConfig.class)
public class RandomServiceTest {

    @Autowired
    private RandomService randomService;

    @Test
    public void randomGenerateShouldReturnUuid() throws Exception {

        // Given, When
        ActorInfo actorInfo = randomService.randomGenerate(5);

        // Then
        Identifier identifier = actorInfo.identifier();

        assertThat(identifier.getId()).isNotNull();
    }

    @Test
    public void randomGenerateResponseShouldReturnFiveValues() throws Exception {

        // Given
        ActorInfo actorInfo = randomService.randomGenerate(5);

        // When
        RandomGenerateResponseList responseList = randomService.randomGenerateResponse(actorInfo);

        // Then
        assertThat(responseList).isNotNull();
        assertThat(responseList.getRandomResults().size()).isEqualTo(5);
        assertThat(responseList.getAdvisoryMessage()).isNull();
        assertThat(responseList.getServiceResponseCode()).isEqualTo(ServiceResponseCode.OK);
    }
}
