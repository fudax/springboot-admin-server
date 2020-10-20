package com.fudax.sep.octopus.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fudax.sep.octopus.detector.CustomMailNotifier;
import com.fudax.sep.octopus.detector.MyQueryIndexEndpointStrategy;
import com.fudax.sep.octopus.detector.SystemMetricCheck;
import com.fudax.sep.octopus.detector.SystemMetricListener;
import de.codecentric.boot.admin.server.config.AdminServerProperties;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.services.EndpointDetector;
import de.codecentric.boot.admin.server.services.InstanceRegistry;
import de.codecentric.boot.admin.server.services.endpoints.ChainingStrategy;
import de.codecentric.boot.admin.server.services.endpoints.ProbeEndpointsStrategy;
import de.codecentric.boot.admin.server.web.client.InstanceWebClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuyi4
 */
@Configuration
@EnableConfigurationProperties({AdminServerProperties.class, SystemMetricProperties.class})
public class ServerAutoConfiguration {

    private final AdminServerProperties adminServerProperties;

    public ServerAutoConfiguration(AdminServerProperties adminServerProperties) {
        this.adminServerProperties = adminServerProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public EndpointDetector endpointDetector(InstanceRepository instanceRepository, InstanceWebClient.Builder instanceWebClientBuilder) {
        InstanceWebClient instanceWebClient = instanceWebClientBuilder.build();
        ChainingStrategy strategy = new ChainingStrategy(
                new MyQueryIndexEndpointStrategy(instanceWebClient),
                new ProbeEndpointsStrategy(instanceWebClient, this.adminServerProperties.getProbedEndpoints())
        );
        return new EndpointDetector(instanceRepository, strategy);
    }

    @Bean
    public SystemMetricCheck systemMetricCheck(InstanceRegistry registry,
                                               SystemMetricProperties systemMetricProperties,
                                               CustomMailNotifier mailNotifier,
                                               ObjectMapper mapper) {
        return new SystemMetricCheck(registry, systemMetricProperties, mailNotifier, mapper);
    }

    @Bean
    public SystemMetricListener systemMetricListener(SystemMetricCheck systemMetricCheck) {
        return new SystemMetricListener(systemMetricCheck);
    }

}
