package com.shifter.shifter_back.security.jwt;

import com.shifter.shifter_back.exceptions.details.ExceptionDetails;
import com.shifter.shifter_back.exceptions.details.TokenExpiredExceptionDetails;
import com.shifter.shifter_back.services.auth.UserService;
import com.shifter.shifter_back.utils.ExceptionUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getEmailFromJwtToken(jwt);

                UserDetails userDetails = userService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            logger.error("Cannot set user authentication: {}", ex);
            HttpStatus status = HttpStatus.UNAUTHORIZED;

            ExceptionDetails details = ExceptionDetails.createExceptionDetails(ex, status, "Unauthorized.");

            TokenExpiredExceptionDetails tokenDetails = TokenExpiredExceptionDetails
                    .builder()
                    .title(details.getTitle())
                    .details(details.getDetails())
                    .status(status.value())
                    .timestamp(details.getTimestamp())
                    .developerMessage(details.getDeveloperMessage())
                    .expired(true)
                    .build();

            response.setStatus(status.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(ExceptionUtils.convertObjectToJson(tokenDetails));

        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());

            filterChain.doFilter(request, response);
        }


    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.isNotEmpty(headerAuth) && StringUtils.isNotBlank(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}