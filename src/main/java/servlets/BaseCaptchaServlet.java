package servlets;

import services.captcha.CaptchaService;
import services.register.RegisterService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static servlets.ResponseHelper.*;

public class BaseCaptchaServlet extends HttpServlet {
    protected RegisterService registerService;
    protected CaptchaService captchaService;

    public BaseCaptchaServlet(RegisterService registerService, CaptchaService capService) {
        this.registerService = registerService;
        this.captchaService = capService;
    }

    public boolean isRegisteredUser(HttpServletResponse resp, String key) throws IOException {
        if (key == null) {
            publicKeyNull(resp);
            return false;
        }

        UUID publicKey = UUID.fromString(key);
        if (!registerService.isRegistered(publicKey)) {
            notRegisteredUser(resp);
            return false;
        }

        return true;
    }

    public boolean isValidToken(HttpServletResponse resp, String publicKey, String token) throws IOException {
        if (!captchaService.isValidToken(UUID.fromString(publicKey), token)) {
            invalidToken(resp);
            return false;
        }
        return true;
    }
}
