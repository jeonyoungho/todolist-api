package com.example.util;

import com.example.exception.CustomException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import static com.example.exception.ErrorCode.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtil {

    public static String getCurrentAccountId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !StringUtils.hasText(authentication.getName())) {
            throw new CustomException(NOT_FOUND_AUTHENTICATION_INFO);
        }

        return authentication.getName();
    }

    public static void checkValidRequest(String accountId) {
        if (accountId == null) {
            throw new CustomException(MEMBER_NOT_FOUND);
        }

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !StringUtils.hasText(authentication.getName())) {
            throw new CustomException(NOT_FOUND_AUTHENTICATION_INFO);
        }

        if (!authentication.getName().equals(accountId)) {
            throw new CustomException(INVALID_REQUEST);
        }
    }
}
