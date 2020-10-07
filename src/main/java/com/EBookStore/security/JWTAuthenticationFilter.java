package com.EBookStore.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


public class JWTAuthenticationFilter extends OncePerRequestFilter{

	@Autowired
	private  JwtUtils jwtUtils;
	@Autowired
	private  UserDetailsService userDetailsService;
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain)
											throws ServletException, IOException {
		String jwt = getJWTFromRequest(request);
		String username = jwtUtils.getUsernameFromToken(jwt);
		 if (username!=null) {

	            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
	            if (jwtUtils.validateToken(jwt, userDetails)) {
	                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
	                        userDetails, null, userDetails.getAuthorities());
	                authentication.setDetails(new WebAuthenticationDetailsSource()
	                        .buildDetails(request));
	                SecurityContextHolder.getContext().setAuthentication(
	                        authentication);
	            }
	        }
		 
		
		filterChain.doFilter(request, response);
		
	}

	/**
	 * @return token without "Bearer"
	 */
	private String getJWTFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
			return bearerToken.substring(7);
		}
		return bearerToken;
	}
	
}
