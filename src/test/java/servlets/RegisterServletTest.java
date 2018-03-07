package servlets;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import org.mockito.Mockito;
import services.register.KeyGenerator;
import services.register.RegisterService;
import store.RuntimeStore;
import store.Store;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RegisterServletTest extends Mockito {
    @Test
    public void testServlet() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        Store store = new RuntimeStore();
        new RegisterServlet(new RegisterService(store)).doPost(request, response);

        writer.flush();

        JSONParser parser = new JSONParser();
        String jsonString = stringWriter.toString();
        JSONObject obj = (JSONObject) parser.parse(jsonString);
        String publicKey = (String) obj.get(Protocol.PUBLIC_KEY);
        String secretKey = (String) obj.get(Protocol.SECRET);

        assertEquals(KeyGenerator.getPublicKey(secretKey).toString(), publicKey);
        assertTrue(store.contains(UUID.fromString(publicKey)));
    }

}