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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

import static org.junit.Assert.*;

public class BaseCaptchaServletTest extends Mockito {
    HttpServletResponse response = mock(HttpServletResponse.class);
    BaseCaptchaServlet baseCaptcha;
    RegisterService registerService;
    CaptchaService captchaService;
    StringWriter writer;
    User user;

    @Before
    public void prepare() throws IOException {
        registerService = new RegisterService(new RuntimeStore());
        user = registerService.registryUser();
        captchaService = new CaptchaService(1000);

        baseCaptcha = new BaseCaptchaServlet(registerService, captchaService);

        writer = new StringWriter();
        PrintWriter pwriter = new PrintWriter(writer);
        when(response.getWriter()).thenReturn(pwriter);
    }

    @Test
    public void notRegisteredUser() throws Exception {
        boolean status = baseCaptcha.isRegisteredUser(response, UUID.randomUUID().toString());

        writer.flush();

        JSONParser parser = new JSONParser();
        String jsonString = writer.toString();
        JSONObject obj = (JSONObject) parser.parse(jsonString);

        String error = (String) obj.get(Protocol.ERROR_CODE);

        assertNotNull(error);
        assertFalse(status);
    }

    @Test
    public void registeredUser() throws Exception {
        boolean status = baseCaptcha.isRegisteredUser(response, user.getPublicKey().toString());

        assertTrue(status);
    }

    @Test
    public void validToken() throws Exception {
        Captcha captcha = captchaService.createCaptcha(user.getPublicKey());

        boolean status = baseCaptcha.isValidToken(response, user.getPublicKey().toString(), captcha.getToken());

        assertTrue(status);
    }

    @Test
    public void invalidToken() throws Exception {
        boolean status = baseCaptcha.isValidToken(response, user.getPublicKey().toString(), "42");

        writer.flush();

        JSONParser parser = new JSONParser();
        String jsonString = writer.toString();
        JSONObject obj = (JSONObject) parser.parse(jsonString);

        String error = (String) obj.get(Protocol.ERROR_CODE);

        assertNotNull(error);
        assertFalse(status);
    }
}