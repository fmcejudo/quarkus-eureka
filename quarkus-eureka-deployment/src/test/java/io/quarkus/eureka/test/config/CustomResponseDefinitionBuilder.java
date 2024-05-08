package io.quarkus.eureka.test.config;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import com.github.tomakehurst.wiremock.common.FileSource;

import static com.github.tomakehurst.wiremock.core.WireMockApp.FILES_ROOT;

import java.util.Map;

public class CustomResponseDefinitionBuilder extends ResponseDefinitionBuilder {

    public static CustomResponseDefinitionBuilder aCustomResponse() {
        return new CustomResponseDefinitionBuilder();
    }

    @Override
    public CustomResponseDefinitionBuilder withHeader(String key, String... values) {
        super.withHeader(key, values);
        return this;
    }

    @Override
    public CustomResponseDefinitionBuilder withStatus(int status) {
        super.withStatus(status);
        return this;
    }

    public CustomResponseDefinitionBuilder withBodyFromFileReplacing(String fileName, Map<String, String> replaceable) {
        FileSource fileSource = new ClasspathFileSource(FILES_ROOT);
        String content = fileSource.getTextFileNamed(fileName).readContentsAsString();
        for (Map.Entry<String, String> entry : replaceable.entrySet()) {
            content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        this.withBody(content);
        return this;
    }

}
