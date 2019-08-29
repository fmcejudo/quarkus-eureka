package io.quarkus.eureka.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;


public class DataCenterInfoTest {

    @Test
    public void shouldParseDataCenterInfo() throws JsonProcessingException {
        DataCenterInfo dataCenterInfo = () -> DataCenterInfo.Name.MyOwn;
        String json = new ObjectMapper().writeValueAsString(dataCenterInfo);
        System.out.println(json);
    }
}