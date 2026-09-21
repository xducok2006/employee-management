package com.example.employee_manage_project.service;

import com.example.employee_manage_project.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtEncoder jwtEncoder;
    public String generateToken(User user)
    {
        Instant now = Instant.now();
        Instant validity = now.plus(1, ChronoUnit.HOURS);
        List<String> roles = new ArrayList<>();
        user.getRoles().forEach(role -> roles.add(role.getName()));
        JwtClaimsSet claimsSet = JwtClaimsSet.builder().subject(user.getUsername()).
                issuedAt(now).expiresAt(validity).claim("roles",roles).build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header,claimsSet)).getTokenValue();
    }
    public String getCurrentUser()
    {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
