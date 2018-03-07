package servlets;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import services.captcha.Captcha;
import services.captcha.CaptchaService;
import services.captcha.VerifyService;
import services.register.RegisterService;
import services.register.User;
import store.RuntimeStore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.*;

public class CaptchaVerifyServletTest extends Mockito {
    VerifyService verifyService;
    RegisterService registerService;
    User user;
    StringWriter writer;
    CaptchaService captchaService;
    CaptchaVerifyServlet captchaVerify;
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    @Before
    public void prepare() throws IOException {
        registerService = new RegisterService(new RuntimeStore());
        captchaService = new CaptchaService(1000);
        verifyService = new VerifyService(captchaService);
        user = registerService.registryUser();
        captchaVerify = new CaptchaVerifyServlet(verifyService);

        writer = new StringWriter();
        PrintWriter pwriter = new PrintWriter(writer);
        when(response.getWriter()).thenReturn(pwriter);
    }

    @Test
    public void successVerify() throws Exception {
        Captcha captcha = captchaService.createCaptcha(user.getPublicKey());

        String verifyToken = verifyService.verifyCaptcha(user.getPublicKey(),
                captcha.getAnswer(), captcha.getToken());

        when(request.getParameter(Protocol.SECRET)).thenReturn(user.getSecretKey().toString());
        when(request.getParameter(Protocol.RESPONSE)).thenReturn(verifyToken);

        captchaVerify.doGet(request, response);

        writer.flush();

        JSONParser parser = new JSONParser();
        String jsonString = writer.toString();
        JSONObject obj = (JSONObject) parser.parse(jsonString);

        boolean status = (boolean) obj.get(Protocol.STATUS);
        assertTrue(status);
    }

    @Test
    public void failedVerify() throws Exception {
        Captcha captcha = captchaService.createCaptcha(user.getPublicKey());

        String verifyToken = verifyService.verifyCaptcha(user.getPublicKey(),
                captcha.getAnswer(), captcha.getToken());

        when(request.getParameter(Protocol.SECRET)).thenReturn(user.getSecretKey().toString());
        when(request.getParameter(Protocol.RESPONSE)).thenReturn(verifyToken + "$");

        captchaVerify.doGet(request, response);

        writer.flush();

        JSONParser parser = new JSONParser();
        String jsonString = writer.toString();
        JSONObject obj = (JSONObject) parser.parse(jsonString);

        String error = (String) obj.get(Protocol.ERROR_CODE);
        boolean status = (boolean) obj.get(Protocol.STATUS);
        assertFalse(status);
        assertNotNull(error);
    }
}