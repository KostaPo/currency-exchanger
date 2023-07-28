package ru.kostapo.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoggingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("NEW REQUEST " +
                "method:"+((HttpServletRequest) request).getMethod() +
                " uri:"+((HttpServletRequest) request).getRequestURI());

        chain.doFilter(request, response);

        System.out.println("Response: " + ((HttpServletResponse) response));
    }

    @Override
    public void destroy() {}
}
