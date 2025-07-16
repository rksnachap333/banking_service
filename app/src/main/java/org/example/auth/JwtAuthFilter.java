package org.example.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.services.JwtService;
import org.example.services.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter
{

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final UserDetailServiceImpl userDetailServiceImpl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {

        String path = request.getServletPath();
        if (path.equals("/auth/v1/signup") || path.equals("/auth/v1/login") || path.equals("/auth/v1/refreshToken")) {
            filterChain.doFilter(request, response); // Skip for public endpoints
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(username);
            System.out.println("username: " + username);
            System.out.println("userDetails: " + userDetails);
            System.out.println("Token valid? " + jwtService.validateToken(token, userDetails));
            System.out.println("Authorities: " + userDetails.getAuthorities());
            if(jwtService.validateToken(token,userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                System.out.println("Auth set in SecurityContextHolder");
            } else{
                new ResponseEntity<>("Token Expired", HttpStatus.UNAUTHORIZED);
            }
        } else {
            System.out.println("I am not doing anything");
        }

        filterChain.doFilter(request, response);

    }
}
