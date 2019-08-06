package io.quarkus.eureka.client;

import org.junit.jupiter.api.Test;

import javax.json.bind.JsonbBuilder;

public class DataCenterInfoTest {

    @Test
    public void shouldParseDataCenterInfo() {
        DataCenterInfo dataCenterInfo = () -> DataCenterInfo.Name.MyOwn;
        String json = JsonbBuilder.create().toJson(dataCenterInfo);
        System.out.println(json);
    }
}