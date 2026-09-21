package com.example.employee_manage_project.service;

import com.example.employee_manage_project.entity.RefreshToken;
import com.example.employee_manage_project.entity.User;
import com.example.employee_manage_project.exception.HandleNotFound;
import com.example.employee_manage_project.exception.RefreshTokenExpiredException;
import com.example.employee_manage_project.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    public String generateSecureToken()
    {
        byte[] random = new byte[64];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(random);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(random);
    }

    public RefreshToken refreshToken(User user)
    {
        String token = generateSecureToken();
        RefreshToken refreshToken = RefreshToken.builder().
                token(token).expiryDate(Instant.now().plus(7, ChronoUnit.DAYS)).user(user).build();
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken)
    {
        if(refreshToken.getExpiryDate().isBefore(Instant.now()))
        {
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenExpiredException("Token hết hạn");
        }
        return refreshToken;
    }

    public String refreshAccessToken(String token)
    {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(()->new HandleNotFound("Không tìm thấy token"));
        verifyExpiration(refreshToken);
        User user = refreshToken.getUser();
        return jwtService.generateToken(user);
    }

    public void deleteRefreshToken(String token)
    {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(()->new HandleNotFound("Không tìm thấy token"));
        refreshTokenRepository.delete(refreshToken);
    }
}
