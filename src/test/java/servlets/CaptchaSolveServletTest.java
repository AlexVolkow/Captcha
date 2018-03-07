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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CaptchaSolveServletTest extends Mockito {
    RegisterService registerService;
    CaptchaService captchaService;
    CaptchaSolveServlet solveServlet;
    VerifyService verifyService;
    User user;
    StringWriter writer;
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);


    @Before
    public void prepare() throws IOException {
        registerService = new RegisterService(new RuntimeStore());
        user = registerService.registryUser();
        captchaService = new CaptchaService(1000);
        verifyService = new VerifyService(captchaService);

        solveServlet = new CaptchaSolveServlet(registerService, verifyService, captchaService);

        writer = new StringWriter();
        PrintWriter pwriter = new PrintWriter(writer);
        when(response.getWriter()).thenReturn(pwriter);
    }

    @Test
    public void rightAnswer() throws Exception {
        Captcha captcha = captchaService.createCaptcha(user.getPublicKey());

        when(request.getParameter(Protocol.PUBLIC_KEY)).thenReturn(user.getPublicKey().toString());
        when(request.getParameter(Protocol.TOKEN)).thenReturn(captcha.getToken());
        when(request.getParameter(Protocol.ANSWER)).thenReturn(captcha.getAnswer());

        solveServlet.doPost(request, response);

        writer.flush();

        JSONParser parser = new JSONParser();
        String jsonString = writer.toString();
        JSONObject obj = (JSONObject) parser.parse(jsonString);

        String token = (String) obj.get(Protocol.RESPONSE);
        assertTrue(verifyService.verifyToken(user.getSecretKey().toString(), token));
    }

    @Test
    public void wrongAnswer() throws Exception {
        Captcha captcha = captchaService.createCaptcha(user.getPublicKey());

        when(request.getParameter(Protocol.PUBLIC_KEY)).thenReturn(user.getPublicKey().toString());
        when(request.getParameter(Protocol.TOKEN)).thenReturn(captcha.getToken());
        when(request.getParameter(Protocol.ANSWER)).thenReturn("#$");

        solveServlet.doPost(request, response);

        writer.flush();

        JSONParser parser = new JSONParser();
        String jsonString = writer.toString();
        JSONObject obj = (JSONObject) parser.parse(jsonString);

        String error = (String) obj.get(Protocol.ERROR_CODE);

        assertNotNull(error);
    }
}