package services.captcha;

import org.junit.Before;
import org.junit.Test;
import services.register.KeyGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

public class CaptchaServiceTest {
    CaptchaService captchaService;
    UUID user;

    @Before
    public void prepare() {
        captchaService = new CaptchaService(1000);
        user = KeyGenerator.getPublicKey(KeyGenerator.getSecretKey());
    }

    @Test
    public void newCaptcha() {
        Captcha captcha = captchaService.createCaptcha(user);
        assertTrue(captchaService.isValidToken(user, captcha.getToken()));
    }

    @Test
    public void uniqueToken() {
        Set<String> tokens = new HashSet<>();
        for (int i = 0; i < 10_000; i++) {
            Captcha captcha = captchaService.createCaptcha(user);
            tokens.add(captcha.getToken());
        }
        assertEquals(tokens.size(), 10_000);
    }

    @Test
    public void simpleRemove() {
        Captcha captcha = captchaService.createCaptcha(user);

        captchaService.removeCaptcha(user, captcha.getToken());
        assertFalse(captchaService.isValidToken(user, captcha.getToken()));
        assertNull(captchaService.getCaptcha(user, captcha.getToken()));
    }

    @Test
    public void removeWithCallback() {
        final boolean[] callback = {false};
        captchaService = new CaptchaService(captcha -> callback[0] = true, 2000);
        Captcha captcha = captchaService.createCaptcha(user);
        captchaService.removeCaptcha(user, captcha.getToken());
        assertTrue(callback[0]);
    }

    @Test
    public void oneCaptchaForUser() {
        Captcha c1 = captchaService.createCaptcha(user);
        Captcha c2 = captchaService.createCaptcha(user);

        assertTrue(captchaService.isValidToken(user, c2.getToken()));
        assertFalse(captchaService.isValidToken(user, c1.getToken()));
    }

    @Test
    public void testTtl() throws InterruptedException {
        Captcha captcha = captchaService.createCaptcha(user);

        Thread.sleep(1000);

        assertFalse(captchaService.isValidToken(user, captcha.getToken()));
    }

    @Test
    public void getCaptcha() {
        Captcha captcha = captchaService.createCaptcha(user);

        Captcha c = captchaService.getCaptcha(user, captcha.getToken());

        assertEquals(c, captcha);
    }
}