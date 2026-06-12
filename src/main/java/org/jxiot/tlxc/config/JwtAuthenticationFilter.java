package org.jxiot.tlxc.config;

import org.jxiot.tlxc.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                if (jwtUtil.validateToken(token)) {
                    Integer userId = jwtUtil.getUserIdFromToken(token);
                    request.setAttribute("userId", userId);
                    request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
                    request.setAttribute("role", jwtUtil.getRoleFromToken(token));
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"Invalid token\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}