package ru.practicum.explore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.explore.model.EndpointHitDto;
import ru.practicum.explore.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StatClient {
    protected final RestTemplate rest;
    private final String serverUrl;

    @Autowired
    public StatClient(@Value("${ewm-server.url}") String url) {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
        this.serverUrl = url;
    }

    public void save(EndpointHitDto dto) {
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(dto, defaultHeaders());
        rest.exchange(serverUrl + "/hit",
                HttpMethod.POST,
                requestEntity,
                Object.class);
    }

    public ResponseEntity<List<ViewStats>> get(LocalDateTime start, LocalDateTime end,
                                               Boolean unique, List<String> uris) {
        Map<String, Object> parameters = Map.of(
                "start", start.toString().replace("T", " "),
                "end", end.toString().replace("T", " "),
                "unique", unique,
                "uris", String.join(",", uris)
        );
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", "{start}")
                .queryParam("end", "{end}")
                .queryParam("unique", "{unique}")
                .queryParam("uris", "{uris}")
                .encode()
                .toUriString();
        return makeAndSendRequest(HttpMethod.GET, urlTemplate, parameters);
    }

    private <T> ResponseEntity<List<ViewStats>> makeAndSendRequest(HttpMethod method, String path, Map<String, Object> parameters) {
        HttpEntity<T> requestEntity = new HttpEntity<>(defaultHeaders());

        ResponseEntity<List<ViewStats>> serverResponse;
        log.info("Parameters: {}", parameters);
        serverResponse = rest.exchange(path, method, requestEntity, new ParameterizedTypeReference<>() {
        }, parameters);
        log.info("Response contains body {}", serverResponse.getBody() != null);
        return serverResponse;
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
