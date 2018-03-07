package services.captcha;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CaptchaService {
    private final static Logger logger = LogManager.getLogger(CaptchaService.class.getName());
    private static final int CAPTCHA_LENGTH = 8;

    private Map<UUID, Captcha> captches = new ConcurrentHashMap<>();
    private CaptchaCallback callback;
    private long ttl;

    public CaptchaService(CaptchaCallback callback, long ttl) {
        this.callback = callback;
        this.ttl = ttl;
    }

    public CaptchaService(long ttl) {
        this.ttl = ttl;
    }

    public Captcha createCaptcha(UUID user) {
        String token = TokenUtils.nextToken();
        String answer = TokenUtils.randomToken(CAPTCHA_LENGTH);

        Captcha captcha = new Captcha(token, answer);
        Captcha prev = captches.put(user, captcha);

        if (prev != null) {
            removeCaptcha(user, prev.getToken());
        }

        logger.info("Create new captcha with token " + token);

        return captcha;
    }

    public boolean isValidToken(UUID user, String token) {
        if (captches.containsKey(user)) {
            Captcha captcha = captches.get(user);
            if (captcha.getToken().equals(token)) {
                if (captcha.isValid(ttl)) {
                    return true;
                } else {
                    removeCaptcha(user, token);
                }
            }
        }
        return false;
    }

    public Captcha getCaptcha(UUID user, String token) {
        Captcha captcha = captches.get(user);
        if (captcha != null && captcha.getToken().equals(token)) {
            return captcha;
        }
        return null;
    }

    public Captcha getCaptcha(String publicKey, String token) {
        return getCaptcha(UUID.fromString(publicKey), token);
    }

    public void removeCaptcha(UUID user, String token) {
        Captcha captcha = captches.get(user);
        if (captcha.getToken().equals(token)) {
            if (callback != null) {
                callback.removeCaptcha(captcha);
            }
            captches.remove(user);
            logger.info("Remove captcha with token " + captcha.getToken());
        }
    }

    public interface CaptchaCallback {
        void removeCaptcha(Captcha captcha);
    }
}
