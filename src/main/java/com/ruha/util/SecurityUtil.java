package com.ruha.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Slf4j
public class SecurityUtil {

    private SecurityUtil() {
    }

    public static Optional<Long> getLoginMemberId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("Security Context에 인증 정보가 없습니다.");
            return Optional.empty();
        }

        String username = authentication.getName();

        if (!authentication.isAuthenticated() || "anonymousUser".equals(username)) {
            log.debug("인증되지 않은 사용자입니다.");
            return Optional.empty();
        }

        try {
            return Optional.of(Long.parseLong(username));
        } catch (NumberFormatException e) {
            log.error("인증 정보(principal)가 Long 타입이 아닙니다. principal: {}", username);
            return Optional.empty();
        }
    }
}
