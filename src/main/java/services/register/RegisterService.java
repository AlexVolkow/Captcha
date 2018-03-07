package services.register;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import store.Store;

import java.util.UUID;

public class RegisterService {
    private final static Logger logger = LogManager.getLogger(RegisterService.class.getName());

    private Store store;

    public RegisterService(Store store) {
        this.store = store;
    }

    public User registryUser() {
        UUID secretKey = KeyGenerator.getSecretKey();
        UUID publicKey = KeyGenerator.getPublicKey(secretKey);

        store.addUser(publicKey);
        logger.info("Create new user with public key " + publicKey);

        return new User(secretKey, publicKey);
    }

    public boolean isRegistered(UUID publicKey) {
        return store.contains(publicKey);
    }
}
