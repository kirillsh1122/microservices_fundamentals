package com.microservice.architecture.overview.resource_processor.integration;


import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/resource_processor_flow.feature")
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "com.microservice.architecture.overview.resource_processor.integration"
)
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, summary, json:target/reports/cucumber-reports/cucumber.json,")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@component")
public class ResourcekafkaListenerTestsRunner {
}
