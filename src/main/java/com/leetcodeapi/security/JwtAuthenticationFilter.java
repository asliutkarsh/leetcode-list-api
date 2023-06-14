package com.leetcodeapi.security;

import com.leetcodeapi.utils.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtTokenUtil jwtTokenUtil;

    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Get the Bearer token from the request header
        String header = request.getHeader("Authorization");
        String username = null;
        String token = null;
        if (header != null && header.startsWith("Bearer")){
            //Extracting jwt Token
            token = header.substring(7);
            //verifying jwtToken also getting mobile out of token
            try {
                username = jwtTokenUtil.getUsernameFromToken(token);
            }catch (IllegalArgumentException e){
                log.error("Unable to get JWT Token");
            }catch (ExpiredJwtException e){
                log.error("JWT Token has expired");
            }catch (MalformedJwtException e){
                log.error("Malformed JWT From Auth Filter");
            }
        }else {
            log.error("JWT Token does not begin with Bearer or is not present");
        }

        //checking if jwt do return a mobile ,i.e, jwt is from our secret
        //also checking if request is already authenticated, if not then only proceed
        if (username != null && SecurityContextHolder.getContext().getAuthentication()==null){
            //getting userDetail from jwtTokenUtil
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                //validating jwt token
                if (jwtTokenUtil.validateToken(token, userDetails)){
                    var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    //setting SecurityContext as authenticated and returning with required authorization
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else throw new JwtException("Invalid Token");
        }
        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        var path = request.getServletPath();
        return (path.startsWith("/api/v1/auth") || path.startsWith("/actuator/health"));
    }
}
