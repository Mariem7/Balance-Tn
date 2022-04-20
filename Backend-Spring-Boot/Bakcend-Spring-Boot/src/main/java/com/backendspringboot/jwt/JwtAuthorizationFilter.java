package com.backendspringboot.jwt;

import com.backendspringboot.constant.SecurityConstant;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

//Spring guarantees that the OncePerRequestFilter is executed only once for a given request.
//A filter may be invoked as part of a REQUEST or ASYNC dispatches that occur in separate threads.
// We should use OncePerRequestFilter since we are doing a database call to retrieve the principal or
// the authenticated user, there is no point in doing this more than once. After that, we set the principal to the security context.
@AllArgsConstructor
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private JWTTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //we will check if the request method if OPTIONS, if yes then we set the response of the http request to OK
        //we do that because the OPTIONS_HTTP_METHOD is send before every request to gather information about the server
        if(request.getMethod().equalsIgnoreCase(SecurityConstant.OPTIONS_HTTP_METHOD)){
            response.setStatus(HttpStatus.OK.value());
        }else{
            //check if the header si null or don't start with the prefix (Bearer)
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if(authorizationHeader == null || !authorizationHeader.startsWith(SecurityConstant.TOKEN_PREFIX)){
                //if it is the case then filter the request
                filterChain.doFilter(request,response);
                //to stop the execution of the method
                return;
            }
            //else we will get the token
            //we will extract the prefix (Bearer from the token)
            String token = authorizationHeader.substring(SecurityConstant.TOKEN_PREFIX.length());
            //we will get the username from the token
            String username = jwtTokenProvider.getSubject(token);
            //we will check if the token is valid
            if (jwtTokenProvider.isTokenValid(username,token) && SecurityContextHolder.getContext().getAuthentication() == null){
                List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
                //when we get the username, authorities and the request we can get the authentication object
                Authentication authentication = jwtTokenProvider.getAuthentication(username,authorities,request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else{
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request,response);
    }
}
