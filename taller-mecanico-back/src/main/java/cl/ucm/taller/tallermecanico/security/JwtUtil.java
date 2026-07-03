package cl.ucm.taller.tallermecanico.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    @Value("${jwt.secret:mySuperSecretKey12345}")
    private String secretKey;

    // CREAR token según Rúbrica (Claims: sub, roles, iat, exp)
    public String create(String username, String roles) {
        Algorithm ALGORITHM = Algorithm.HMAC256(secretKey);

        return JWT.create()
                .withSubject(username) // Claim "sub"
                .withClaim("roles", roles) // Claim "roles"
                .withIssuedAt(new Date()) // Claim "iat"
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(15))) // Claim "exp"
                .sign(ALGORITHM);
    }

    public boolean isValid(String jwt) {
        try {
            Algorithm ALGORITHM = Algorithm.HMAC256(secretKey);
            JWT.require(ALGORITHM)
                    .build()
                    .verify(jwt);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public String getUsername(String jwt) {
        // Extraemos el username directamente del subject (sub)
        return JWT.decode(jwt).getSubject();
    }
}
