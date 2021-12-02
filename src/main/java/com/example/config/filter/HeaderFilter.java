package com.example.config.filter;


import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HeaderFilter implements Filter {

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
       final HttpServletResponse res = (HttpServletResponse) response;
       res.setHeader("Access-Control-Allow-Origin", "*");
       res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE");
       res.setHeader("Access-Control-Max-Age", "3600");
       res.setHeader("Access-Control-Allow-Headers",
               "X-Requested-With, Content-Type, Authorization, X-XSRF-token"
       );
       res.setHeader("Access-Control-Allow-Credentials", "true"); // 브라우저에서 자격 증명을 허용 여부 (ex. cookie, 다른 Origin으로부터 쿠키를 허용하도록 지정하려면 true로 바꿔줘야함)

       chain.doFilter(request, response);
    }
}
