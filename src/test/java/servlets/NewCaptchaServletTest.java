package servlets;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import services.captcha.Captcha;
import services.captcha.CaptchaService;
import services.register.RegisterService;
import services.register.User;
import store.RuntimeStore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NewCaptchaServletTest extends Mockito {
    RegisterService registerService;
    CaptchaService captchaService;
    NewCaptchaServlet newCaptchaServlet;
    User user;
    StringWriter writer;
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);


    @Before
    public void prepare() throws IOException {
        registerService = new RegisterService(new RuntimeStore());
        user = registerService.registryUser();
        captchaService = new CaptchaService(1000);

        writer = new StringWriter();
        PrintWriter pwriter = new PrintWriter(writer);
        when(response.getWriter()).thenReturn(pwriter);
    }

    @Test
    public void newCaptchaTest() throws Exception {
        newCaptchaServlet = new NewCaptchaServlet(registerService, captchaService, false);

        when(request.getParameter(Protocol.PUBLIC_KEY)).thenReturn(user.getPublicKey().toString());

        newCaptchaServlet.doGet(request, response);

        writer.flush();

        JSONParser parser = new JSONParser();
        String jsonString = writer.toString();
        JSONObject obj = (JSONObject) parser.parse(jsonString);

        String token = (String) obj.get(Protocol.TOKEN);
        String answer = (String) obj.get(Protocol.ANSWER);

        Captcha captcha = captchaService.getCaptcha(user.getPublicKey(), token);
        assertEquals(captcha.getAnswer(), answer);
    }

    @Test
    public void newCaptchaProduction() throws Exception {
        newCaptchaServlet = new NewCaptchaServlet(registerService, captchaService, true);

        when(request.getParameter(Protocol.PUBLIC_KEY)).thenReturn(user.getPublicKey().toString());

        newCaptchaServlet.doGet(request, response);

        writer.flush();

        JSONParser parser = new JSONParser();
        String jsonString = writer.toString();
        JSONObject obj = (JSONObject) parser.parse(jsonString);

        String token = (String) obj.get(Protocol.TOKEN);

        assertTrue(captchaService.isValidToken(user.getPublicKey(), token));
    }
}