package edu.saferoute.configuracion;

import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import edu.saferoute.Datos_Repository.UsuarioRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final UsuarioRepository usuarios;
    public JwtFilter(JwtService jwt,UsuarioRepository usuarios){this.jwt=jwt;this.usuarios=usuarios;}
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws ServletException,IOException{
        String header=request.getHeader("Authorization");
        if(header!=null&&header.startsWith("Bearer "))try{
            var claims=jwt.validar(header.substring(7));String subject=claims.getSubject();
            if(subject!=null) usuarios.findByCorreo(subject).ifPresent(user->SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(subject,null,List.of(new SimpleGrantedAuthority(user.rol().name())))));
        }catch(JwtException|IllegalArgumentException ignored){SecurityContextHolder.clearContext();}
        chain.doFilter(request,response);
    }
}
