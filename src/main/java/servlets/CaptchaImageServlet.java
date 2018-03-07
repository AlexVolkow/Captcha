package servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.captcha.Captcha;
import services.captcha.CaptchaGenerator;
import services.captcha.CaptchaService;
import services.register.RegisterService;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;

public class CaptchaImageServlet extends BaseCaptchaServlet {
    private final static Logger logger = LogManager.getLogger(CaptchaImageServlet.class.getName());

    public static final String PAGE_URL = "/captcha/image";
    private CaptchaGenerator generator;

    public CaptchaImageServlet(RegisterService registerService, CaptchaService captchaService, CaptchaGenerator generator) {
        super(registerService, captchaService);
        this.generator = generator;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String publicKey = req.getParameter(Protocol.PUBLIC_KEY);
        String token = req.getParameter(Protocol.TOKEN);

        if (isRegisteredUser(resp, publicKey) && isValidToken(resp, publicKey, token)) {
            Captcha captcha = captchaService.getCaptcha(publicKey, token);
            RenderedImage captchaImage = generator.getImage(captcha);

            if (captchaImage == null) {
                ResponseHelper.error(resp, "Error creating captcha",
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            resp.setContentType("image/png");
            try (OutputStream out = resp.getOutputStream()) {
                ImageIO.write(captchaImage, "png", out);
            } catch (IOException e) {
                logger.error("An error occurred while write captcha " + captcha);
                ResponseHelper.error(resp, "An error occurred while write captcha",
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
}
