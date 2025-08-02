package com.ruha.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = JwtTokenUtil.extractTokenFromRequest(request);

            if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {
                Long memberId = jwtProvider.getMemberIdFromToken(token);
                
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        memberId.toString(), 
                        null, 
                        Collections.emptyList()
                );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("JWT 인증 성공. 회원 ID: {}", memberId);
            }
        } catch (Exception e) {
            log.error("JWT 토큰 처리 중 오류 발생: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}