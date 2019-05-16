package ua.edu.chdtu.deanoffice.webstarter.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.webstarter.security.SecurityConstants.HEADER_STRING;
import static ua.edu.chdtu.deanoffice.webstarter.security.SecurityConstants.TOKEN_PREFIX;
import static ua.edu.chdtu.deanoffice.webstarter.security.SecurityConstants.SECRET;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    public JWTAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String token = getToken(req);
        if (token == null) {
            chain.doFilter(req, res);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        String user;
        List<GrantedAuthority> roles = new ArrayList<>();
        if (token != null) {
            try {
                user = Jwts.parser()
                        .setSigningKey(SECRET)
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();
                List<String> roleStrs = Jwts.parser()
                        .setSigningKey(SECRET)
                        .parseClaimsJws(token)
                        .getBody()
                        .get("rol", List.class);
                if (roleStrs != null)
                    roles = roleStrs.stream()
                            .map(role -> new SimpleGrantedAuthority((String)role))
                            .collect(Collectors.toList());
            } catch (JwtException e) {
                return null;
            }
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, roles);
            }
            return null;
        }
        return null;
    }

    private String getToken(HttpServletRequest req) {
        boolean isTokenInHeader = req.getHeader(HEADER_STRING) != null;

        if (isTokenInHeader) {
            return req.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
        } else {
            return req.getParameter("auth-jwt-token");
        }
    }
}
