package todoApp.service;


import todoApp.entity.User;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.apache.shiro.util.ByteSource;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class SecurityUtil {

    @Inject
    private QueryService queryService;

    public static final String HASHED_PASSWORD_KEY = "hashedPassword";
    public static final String SALT_KEY = "salt";
    public static final String BEARER = "Bearer";

    private SecretKey securityKey;

    @PostConstruct
    private void init() {
        securityKey = generateKey();
    }


    public Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


    public boolean passwordsMatch(String dbStoredHashedPassword, String saltText, String clearTextPassword) {
        // Decode salt
        ByteSource salt = ByteSource.Util.bytes(Hex.decode(saltText));
        // Hash password passed by user
        String hashedPassword = hashAndSaltPassword(clearTextPassword, salt);
        // Compare with hash password stored in db
        return hashedPassword.equals(dbStoredHashedPassword);
    }

    public Map<String, String> hashPassword(String clearTextPassword) {
        ByteSource salt = getSalt();
        Map<String, String> credMap = new HashMap<>();
        // Add hashed password an salt to map
        credMap.put(HASHED_PASSWORD_KEY, hashAndSaltPassword(clearTextPassword, salt));
        credMap.put(SALT_KEY, salt.toHex());
        return credMap;


    }

    private String hashAndSaltPassword(String clearTextPassword, ByteSource salt) {
        // Hash password using salt and Sha512 algorithm
        return new Sha512Hash(clearTextPassword, salt, 2000000).toHex();
    }

    private ByteSource getSalt() {
        return new SecureRandomNumberGenerator().nextBytes();
    }


    // Used for authenticate while login
    public boolean authenticateUser(String email, String password) {
        // Try to find user in database using email
        User user = queryService.findUserByEmail(email);
        if (user == null) {
            return false;
        }
        // Check if password passed by user and password of user existing in database
        return passwordsMatch(user.getPassword(), user.getSalt(), password);

    }

    private SecretKey generateKey() {
        // Generate Key using HS512 algorithm
        return MacProvider.generateKey(SignatureAlgorithm.HS512);


    }

    public SecretKey getSecurityKey() {
        return securityKey;
    }
}
