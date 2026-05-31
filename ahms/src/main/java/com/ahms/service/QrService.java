package com.ahms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Service @RequiredArgsConstructor
public class QrService {
    @Value("${python.qr-service.url}") private String qrUrl;
    private final WebClient.Builder builder;

    public String generate(String regId, String name) {
        try {
            Map res = builder.baseUrl(qrUrl).build().post().uri("/generate-qr")
                    .bodyValue(Map.of("registration_id", regId, "patient_name", name))
                    .retrieve().bodyToMono(Map.class).block();
            return res != null ? (String) res.get("qr_path") : null;
        } catch (Exception e) { return null; }
    }

    public byte[] getImage(String regId) {
        try {
            return builder.baseUrl(qrUrl).build().get()
                    .uri("/qr-image/" + regId).retrieve().bodyToMono(byte[].class).block();
        } catch (Exception e) { return null; }
    }
}
