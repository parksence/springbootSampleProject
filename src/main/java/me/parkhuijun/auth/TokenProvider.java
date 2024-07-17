package me.parkhuijun.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import me.parkhuijun.commons.Constant;
import me.parkhuijun.commons.Utils;
import me.parkhuijun.entity.UserRole;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_PREFIX = "Bearer ";
    private final String secretKey;
    private final long tokenPeriodMilliSeconds;
    private final long refreshTokenPeriodMilliSeconds;
    private Key key;

    public TokenProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.token-period-seconds}") long tokenPeriodSeconds,
            @Value("${jwt.refresh-token-period-seconds}") long refreshTokenPeriodSeconds
    ) {
        this.secretKey = secretKey;
        this.tokenPeriodMilliSeconds = tokenPeriodSeconds * 1000;
        this.refreshTokenPeriodMilliSeconds = refreshTokenPeriodSeconds * 1000;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = new Date().getTime();
        Date validity = new Date(now + this.tokenPeriodMilliSeconds);

        AuthUserDetails userDetails = (AuthUserDetails) authentication.getPrincipal();
        System.out.println(Utils.asJsonString(userDetails));
        return BEARER_PREFIX + Jwts.builder()
                .setSubject(authentication.getName())
                .claim("id", userDetails.getId())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();

    }

    public String createRefreshToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = new Date().getTime();
        Date validity = new Date(now + this.refreshTokenPeriodMilliSeconds);

        AuthUserDetails userDetails = (AuthUserDetails) authentication.getPrincipal();

        return BEARER_PREFIX + Jwts.builder()
                .setSubject(authentication.getName())
                .claim("id", userDetails.getId())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Set<UserRole> userAuthorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(roleName -> UserRole.builder().name(roleName).build()) // UserRole 객체를 생성하는 방법에 따라 변경 필요
                .collect(Collectors.toSet());

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        int id = (int) claims.get("id");
        AuthUserDetails principal = new AuthUserDetails(id, claims.getSubject(), null, null, userAuthorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public int validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (SecurityException | MalformedJwtException e) {
            return Constant.TOKEN.EXCEPTION;
        } catch (UnsupportedJwtException e) {
            return Constant.TOKEN.MALFORMED;
        } catch (ExpiredJwtException e) {
            return Constant.TOKEN.EXPIRED;
        }

        return Constant.TOKEN.SUCCESS;
    }
}
