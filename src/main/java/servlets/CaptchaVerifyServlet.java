package servlets;

import org.json.simple.JSONObject;
import services.captcha.VerifyService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CaptchaVerifyServlet extends HttpServlet {
    public static final String PAGE_URL = "/captcha/verify";
    private VerifyService verifyService;

    public CaptchaVerifyServlet(VerifyService verifyService) {
        this.verifyService = verifyService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String secret = req.getParameter(Protocol.SECRET);
        String response = req.getParameter(Protocol.RESPONSE);

        boolean status = false;
        String error = "null";
        try {
            status = verifyService.verifyToken(secret, response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            error = e.getMessage();
        }

        JSONObject json = new JSONObject();
        json.put(Protocol.STATUS, status);
        json.put(Protocol.ERROR_CODE, error);

        ResponseHelper.json(resp, json);
    }
}
