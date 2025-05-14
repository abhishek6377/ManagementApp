package com.jkTech.document.managementApp.dto.response;

import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class JwtResponse {

    private String accessToken;
    private String refreshToken;
    private String type;
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;

    public JwtResponse(String accessToken, Long id, String username, String email, Set<String> roles, String type) {
        this.accessToken = accessToken;
        this.id = id;
        this.type = type;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }


}
