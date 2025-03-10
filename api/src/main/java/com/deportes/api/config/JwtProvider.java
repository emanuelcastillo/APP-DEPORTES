package com.deportes.api.config;


import com.deportes.api.entity.Usuario;
import com.deportes.api.repository.UsuarioRepository;
import com.deportes.api.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${app.jwt.expiration}")
    private Long jwtExpirationDate;

    public String generateToken(Authentication authentication) {
        Usuario user = null;
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            user = usuarioRepository.findByEmail(userDetails.getUsername()).orElse(null);
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate expireDate = currentDate.plusWeeks(jwtExpirationDate);

        return Jwts.builder()
                .subject(user == null ? "none" : user.getEmail())
                .issuedAt(new Date())
                .expiration(Date.from(expireDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .signWith(key())
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUsername(String token) {

        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parse(token);
        return true;

    }
}