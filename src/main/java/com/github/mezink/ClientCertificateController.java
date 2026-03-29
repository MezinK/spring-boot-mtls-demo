package com.github.mezink;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ClientCertificateController {
    @GetMapping("/client-certificate")
    String clientCertificate() {
        return "OK";
    }
}
