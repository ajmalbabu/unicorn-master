package com.unicorn.api;

import com.unicorn.common.actor.Identifier;
import com.unicorn.common.domain.ServiceResponseCode;
import com.unicorn.service.SpringConfig;
import com.unicorn.service.domain.RandomGenerateResponseList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {SpringConfig.class})
@WebIntegrationTest(randomPort = true)
public class UnicornRestApiTest {

    @Value("${local.server.port}")
    private int port;

    @Test
    public void randomGenerateShouldReturnUuid() throws Exception {

        // Given, When
        Identifier identifier = new TestRestTemplate().getForObject(
                "http://localhost:" + this.port + "/unicorn-api/v1/random-generate?count=5", Identifier.class);

        // Then
        assertThat(identifier).isNotNull();
        assertThat(identifier.getId()).isNotNull(); // Of the format => 6d3b26d5-21fd-457a-b1b9-4a7f8fa6bd14
    }

    @Test
    public void randomGenerateResponseShouldReturnFiveValues() throws Exception {

        // Given
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        Identifier identifier = testRestTemplate.getForObject(
                "http://localhost:" + this.port + "/unicorn-api/v1/random-generate?count=5", Identifier.class);

        // When
        RandomGenerateResponseList responseList = testRestTemplate.getForObject(
                "http://localhost:" + this.port + "/unicorn-api/v1/random-generate-response?identifier=" + identifier.getId(), RandomGenerateResponseList.class);

        // Then
        assertThat(responseList.getRandomResults().size()).isEqualTo(5);
        assertThat(responseList.getAdvisoryMessage()).isNull();
        assertThat(responseList.getServiceResponseCode()).isEqualTo(ServiceResponseCode.OK);
    }

}
