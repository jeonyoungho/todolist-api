package com.example.config.interceptor;

import com.example.config.auth.AuthConstants;
import com.example.util.TokenUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtTokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
                             final Object handler) throws IOException {
        final String header = request.getHeader(AuthConstants.AUTH_HEADER);

        if (header != null) {
            final String token = TokenUtils.getTokenFromHeader(header);
            if (TokenUtils.isValidToken(token)) {
                return true;
            }
        }

        // swagger-ui 로부터의 요청일 경우 토큰 유효성 검증을 무시
//        final String referer = request.getHeader("Referer");
//        if (referer.contains("/swagger-ui")) {
//            return true;
//        }

        response.sendRedirect("/error/unauthorized"); // token 유효성 검사에 실패할 경우 예외 API로 리다이렉트
        return false;
    }

}
