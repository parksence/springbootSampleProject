package me.parkhuijun.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import me.parkhuijun.commons.Constant;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
public class JwtFilter extends GenericFilterBean {
    private final TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        logger.info("doFilter");
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String jwt = resolveToken(httpServletRequest);
        System.out.println(jwt);
        String requestURI = httpServletRequest.getRequestURI();
        logger.info(requestURI);

        if (StringUtils.hasText(jwt)) {
            int validCode = tokenProvider.validateToken(jwt);

            if (validCode == Constant.TOKEN.SUCCESS) {
                Authentication authentication = tokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (validCode == Constant.TOKEN.EXPIRED) {
                String refreshToken = httpServletRequest.getHeader("RefreshToken");

                if (refreshToken != null) {
                    refreshToken = refreshToken.substring(7);
                    Authentication authentication = tokenProvider.getAuthentication(refreshToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            logger.info(bearerToken);
            logger.info(bearerToken.substring(7));
            return bearerToken.substring(7);
        }

        return null;
    }
}
