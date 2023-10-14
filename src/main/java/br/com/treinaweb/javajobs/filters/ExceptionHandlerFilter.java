package br.com.treinaweb.javajobs.filters;

import br.com.treinaweb.javajobs.dto.ApiError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {

        try{
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } catch (ExpiredJwtException | SignatureException | MalformedJwtException exception) {
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            LocalDateTime timestamp = LocalDateTime.now();
            String path = httpServletRequest.getRequestURI();

            ApiError apiError = new ApiError(status.value(), timestamp, exception.getLocalizedMessage(), path, null);
            httpServletResponse.setStatus(status.value());
            httpServletResponse.getWriter().write(convertObjectToJson(apiError));
        }

    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if(object == null) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper.writeValueAsString(object);
    }
}
