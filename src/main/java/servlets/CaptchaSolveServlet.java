package servlets;

import org.json.simple.JSONObject;
import services.captcha.CaptchaService;
import services.captcha.VerifyService;
import services.register.RegisterService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;


public class CaptchaSolveServlet extends BaseCaptchaServlet {
    public static final String PAGE_URL = "/captcha/solve";

    private VerifyService verifyService;

    public CaptchaSolveServlet(RegisterService registerService, VerifyService verifyService, CaptchaService captchaService) {
        super(registerService, captchaService);
        this.verifyService = verifyService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String publicKey = req.getParameter(Protocol.PUBLIC_KEY);
        String token = req.getParameter(Protocol.TOKEN);
        String answer = req.getParameter(Protocol.ANSWER);

        if (isRegisteredUser(resp, publicKey) && isValidToken(resp, publicKey, token)) {
            UUID key = UUID.fromString(publicKey);
            String verifyToken = verifyService.verifyCaptcha(key, answer, token);
            if (verifyToken == null) {
                ResponseHelper.error(resp, "Wrong answer", HttpServletResponse.SC_FORBIDDEN);
            } else {
                JSONObject json = new JSONObject();
                json.put(Protocol.RESPONSE, verifyToken);
                ResponseHelper.json(resp, json);
            }
        }
    }
}
