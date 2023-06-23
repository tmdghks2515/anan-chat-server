package io.klovers.server.common.papago;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class PapagoTranslationService {
    private final String papagoApiUrl = "https://openapi.naver.com/v1/papago/n2mt";

    @Value("${papago.client-id}")
    private String clientId;

    @Value("${papago.client-secret}")
    private String clientSecret;

    public String translateText(String text, String targetLang) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        String requestBody = "source=auto&target=" + targetLang + "&text=" + text;
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<PapagoResponse> responseEntity = restTemplate.exchange(
                papagoApiUrl,
                HttpMethod.POST,
                requestEntity,
                PapagoResponse.class
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            PapagoResponse response = responseEntity.getBody();
            if (response != null && response.getMessage() != null) {
                return response.getMessage().getResult().getTranslatedText();
            }
        }

        return null;
    }
}
