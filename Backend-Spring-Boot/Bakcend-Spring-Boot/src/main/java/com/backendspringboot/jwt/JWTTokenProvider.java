package com.backendspringboot.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.backendspringboot.constant.SecurityConstant;
import com.backendspringboot.employee.EmployeePrincipal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import static java.util.Arrays.stream;

//@Component is an annotation that allows Spring to automatically detect our custom beans.
//In other words, without having to write any explicit code, Spring will:
//Scan our application for classes annotated with @Component
//Instantiate them and inject any specified dependencies into them
//Inject them wherever needed
@Component
public class JWTTokenProvider {
    //take the value from the application.properties and pass it into thsi variable
    //for security reason (we don't share for example the application.properties file in the GITHUB
    // for protecting our crucial information)
    //so our jwt secret key will stay secured
    @Value("${jwt.secret}")
    private String secret;

    //when the employee is authenticated, we will pass the employeePrincipal object to provide a token for it
    public String generateJwtToken(EmployeePrincipal employeePrincipal){
        //we need to get the claims (authorities of th employee)
        String[] claims = getClaimsFromEmployee(employeePrincipal);
        return JWT.create()
                .withIssuer(SecurityConstant.CIMF_LLC)
                .withAudience(SecurityConstant.CIMF_ADMINISTRATION)
                .withIssuedAt(new Date())
                .withSubject(employeePrincipal.getUsername())
                .withArrayClaim(SecurityConstant.AUTHORITIES,claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstant.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    //method to return the authorities of the employee
    private String[] getClaimsFromEmployee(EmployeePrincipal employeePrincipal) {
        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority grantedAuthority: employeePrincipal.getAuthorities()){
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }

    //get the authorities of the employee from the token
    public List<GrantedAuthority> getAuthorities(String token){
        String[] claims = getClaimsFromToken(token);
        return stream(claims).map(SimpleGrantedAuthority ::new).collect(Collectors.toList());
    }

    //tell spring security that this user is authenticated so generate the token to it
    public Authentication getAuthentication (String username, List<GrantedAuthority> authorities, HttpServletRequest request){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;
    }

    //return if the token is valid or not
    public boolean isTokenValid(String username, String token){
        JWTVerifier verifier = getJWTVerifier();
        return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier,token);
    }

    //return the subject of the token
    public String getSubject(String token){
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }


    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(SecurityConstant.AUTHORITIES).asArray(String.class);
    }

    //verify if the token is valid or not
    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try{
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT.require(algorithm).withIssuer(SecurityConstant.CIMF_LLC).build();
        }catch(JWTVerificationException exception){
           throw new JWTVerificationException(SecurityConstant.TOKEN_CANNOT_VERIFIED);
        }
        return verifier;
    }
}
