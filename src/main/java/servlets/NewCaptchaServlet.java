package servlets;

import org.json.simple.JSONObject;
import services.captcha.Captcha;
import services.captcha.CaptchaService;
import services.register.RegisterService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;


public class NewCaptchaServlet extends BaseCaptchaServlet {
    public static final String PAGE_URL = "/captcha/new";
    private boolean production;

    public NewCaptchaServlet(RegisterService registerService, CaptchaService captchaService, boolean production) {
        super(registerService, captchaService);
        this.production = production;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String publicKey = req.getParameter(Protocol.PUBLIC_KEY);

        if (isRegisteredUser(resp, publicKey)) {
            UUID user = UUID.fromString(publicKey);
            Captcha captcha = captchaService.createCaptcha(user);

            JSONObject json = new JSONObject();
            json.put(Protocol.TOKEN, captcha.getToken());
            if (!production) {
                json.put(Protocol.ANSWER, captcha.getAnswer());
            }

            ResponseHelper.json(resp, json);
        }
    }
}
