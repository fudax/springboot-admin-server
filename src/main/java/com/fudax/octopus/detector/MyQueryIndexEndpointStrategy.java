package com.fudax.octopus.detector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.values.Endpoint;
import de.codecentric.boot.admin.server.domain.values.Endpoints;
import de.codecentric.boot.admin.server.domain.values.InstanceId;
import de.codecentric.boot.admin.server.domain.values.Registration;
import de.codecentric.boot.admin.server.services.endpoints.EndpointDetectionStrategy;
import de.codecentric.boot.admin.server.services.endpoints.QueryIndexEndpointStrategy;
import de.codecentric.boot.admin.server.web.client.InstanceWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.http.ActuatorMediaType;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.fudax.octopus.constants.OctopusConstants.SPECIAL_CONTEXT;
import static com.fudax.octopus.detector.CustomNameJudge.someContextCanPass;

/**
 * 用于获取客户端的端点指标数据
 */
public class MyQueryIndexEndpointStrategy implements EndpointDetectionStrategy {

    private static final Logger log = LoggerFactory.getLogger(QueryIndexEndpointStrategy.class);
    private final InstanceWebClient instanceWebClient;
    private static Set<String> allowEndpoints = new HashSet<>();

    private static final MediaType actuatorMediaType = MediaType.parseMediaType(ActuatorMediaType.V2_JSON);

    static {
        allowEndpoints.addAll(Arrays.asList("health", "logfile", "info", "metrics"));
    }

    public MyQueryIndexEndpointStrategy(InstanceWebClient instanceWebClient) {
        this.instanceWebClient = instanceWebClient;
    }

    @Override
    public Mono<Endpoints> detectEndpoints(Instance instance) {
        Registration registration = instance.getRegistration();
        String managementUrl = registration.getManagementUrl();
        if (managementUrl == null || Objects.equals(registration.getServiceUrl(), managementUrl)) {
            log.debug("Querying actuator-index for instance {} omitted.", instance.getId());
            return Mono.empty();
        }

        return requestEndpoints(instance, managementUrl);
    }

    private Mono<Endpoints> requestEndpoints(Instance instance, String managementUrl) {
        return this.instanceWebClient.instance(instance)
                .get()
                .uri(managementUrl)
                .exchange()
                .flatMap(this.convert(instance, managementUrl))
                .onErrorResume(e -> {
                    log.warn(
                            "Querying actuator-index for instance {} on '{}' failed: {}",
                            instance.getId(),
                            managementUrl,
                            e.getMessage()
                    );
                    return Mono.empty();
                });
    }

    protected Function<ClientResponse, Mono<Endpoints>> convert(Instance instance, String managementUrl) {
        return response -> {
            if (!response.statusCode().is2xxSuccessful()) {
                return response.bodyToMono(Void.class).then(Mono.empty());
            }

            if (!response.headers().contentType().map(actuatorMediaType::isCompatibleWith).orElse(false)) {
                return response.bodyToMono(Void.class).then(Mono.empty());
            }
            log.debug("Querying actuator-index for instance {} on '{}' successful.", instance.getId(), managementUrl);
            return response.bodyToMono(MyQueryIndexEndpointStrategy.Response.class)
                    .flatMap(r -> convertResponse(r, instance.getRegistration().getName()))
                    .map(this.alignWithManagementUrl(instance.getId(), managementUrl));
        };
    }

    protected Function<Endpoints, Endpoints> alignWithManagementUrl(InstanceId instanceId, String managementUrl) {
        return endpoints -> {
            if (!managementUrl.startsWith("https:")) {
                return endpoints;
            }
            if (endpoints.stream().noneMatch(e -> e.getUrl().startsWith("http:"))) {
                return endpoints;
            }
            return Endpoints.of(endpoints.stream()
                    .map(e -> Endpoint.of(e.getId(), e.getUrl().replaceFirst("http:", "https:")))
                    .collect(Collectors.toList()));
        };
    }

    protected Mono<Endpoints> convertResponse(Response response, final String name) {
        List<Endpoint> endpoints = response.getLinks()
                .entrySet()
                .stream()
                .filter(e -> filter(name, e.getKey(), e.getValue()))
                .map(e -> Endpoint.of(e.getKey(), e.getValue().getHref()))
                .collect(Collectors.toList());
        return endpoints.isEmpty() ? Mono.empty() : Mono.just(Endpoints.of(endpoints));
    }

    /**
     * 过滤客户端的端点。目前对于非能效平台应用只对{allowEndpoints集合中的几种端点开放}
     *
     * @param name
     * @param key
     * @param endpoint
     * @return
     */
    private boolean filter(String name, String key, Response.EndpointRef endpoint) {
        if (someContextCanPass(name,SPECIAL_CONTEXT)) {
            return !key.equals("self") && !endpoint.isTemplated();
        }
        return !key.equals("self") && !endpoint.isTemplated() && allowEndpoints.contains(key);
    }

    protected static class Response {
        @JsonProperty("_links")
        private Map<String, MyQueryIndexEndpointStrategy.Response.EndpointRef> links = new HashMap<>();

        public Map<String, EndpointRef> getLinks() {
            return links;
        }

        public void setLinks(Map<String, EndpointRef> links) {
            this.links = links;
        }

        protected static class EndpointRef {
            private final String href;
            private final boolean templated;

            public String getHref() {
                return href;
            }

            public boolean isTemplated() {
                return templated;
            }

            @JsonCreator
            EndpointRef(@JsonProperty("href") String href, @JsonProperty("templated") boolean templated) {
                this.href = href;
                this.templated = templated;
            }
        }
    }
}