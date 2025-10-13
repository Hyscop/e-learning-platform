package com.elearning.user.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT auth filter
 * 
 * Intercepts every request and validates JWT token
 * 
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException{

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        //Check if Auth header exists and starts with "Bearer "
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            log.debug("No JWT token found in request headers");
            filterChain.doFilter(request, response);
            return;
        }

        //Extractt token from "Bearer <token>"
        jwt = authHeader.substring(7);

        try {
            // Extract email from token
            userEmail = jwtService.extractUsername(jwt);

            //if email exists and user not already authed
            if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){

                //Load user from db
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                //Validate token
                if(jwtService.validateToken(jwt, userDetails)){
                    log.debug("JWT token is valid for user: {}", userEmail);

                    //Create auth object
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    //Set auth in security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    log.warn("JWT token validation failed for user: {}", userEmail);
                }
            }

        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);    

    }
}
