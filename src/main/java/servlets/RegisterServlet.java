package servlets;

import org.json.simple.JSONObject;
import services.register.RegisterService;
import services.register.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RegisterServlet extends HttpServlet {
    public static final String PAGE_URL = "/client/register";
    private RegisterService registerService;

    public RegisterServlet(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = registerService.registryUser();

        JSONObject json = new JSONObject();
        json.put(Protocol.SECRET, user.getSecretKey().toString());
        json.put(Protocol.PUBLIC_KEY, user.getPublicKey().toString());

        ResponseHelper.json(resp, json);
    }
}
