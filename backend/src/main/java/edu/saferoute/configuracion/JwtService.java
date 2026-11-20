package edu.saferoute.configuracion;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {
    private final SecretKey key; private final long ttlMinutes;
    public JwtService(@Value("${saferoute.jwt-secret}") String secret,@Value("${saferoute.jwt-ttl-minutes}") long ttlMinutes){
        if(secret==null||secret.getBytes(StandardCharsets.UTF_8).length<32) throw new IllegalStateException("SAFEROUTE_JWT_SECRET debe tener al menos 32 bytes");
        if(ttlMinutes<1) throw new IllegalStateException("SAFEROUTE_JWT_TTL_MINUTES debe ser positivo");
        this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));this.ttlMinutes=ttlMinutes;
    }
    public String emitir(String correo,String rol){Instant now=Instant.now();return Jwts.builder().subject(correo).claim("rol",rol).issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(ttlMinutes*60))).signWith(key).compact();}
    public io.jsonwebtoken.Claims validar(String token){return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();}
}
