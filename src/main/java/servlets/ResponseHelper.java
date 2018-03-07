package servlets;

import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseHelper {
    public static void publicKeyNull(HttpServletResponse resp) throws IOException {
        error(resp, "Public key is null", HttpServletResponse.SC_BAD_REQUEST);
    }

    public static void notRegisteredUser(HttpServletResponse resp) throws IOException {
        error(resp, "Invalid public key", HttpServletResponse.SC_UNAUTHORIZED);
    }

    public static void invalidToken(HttpServletResponse resp) throws IOException {
        error(resp, "Invalid captcha token", HttpServletResponse.SC_NOT_ACCEPTABLE);
    }

    public static void error(HttpServletResponse resp, String text, int code) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        JSONObject json = new JSONObject();
        json.put(Protocol.ERROR_CODE, text);
        resp.getWriter().println(json);
        resp.setStatus(code);
    }

    public static void json(HttpServletResponse resp, JSONObject json) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        resp.getWriter().println(json);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
