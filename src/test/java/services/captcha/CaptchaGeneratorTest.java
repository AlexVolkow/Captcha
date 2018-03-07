package services.captcha;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class CaptchaGeneratorTest {

    @Test
    public void generateCaptcha() throws IOException {
        CaptchaGenerator generator = new CaptchaGenerator();
        CaptchaService captchaService = new CaptchaService(100);
        Captcha captcha = captchaService.createCaptcha(UUID.randomUUID());

        RenderedImage image = generator.getImage(captcha);
        BufferedImage back = ImageIO.read(new File(CaptchaGenerator.BACKGROUND_PATH));

        assertEquals(image.getHeight(), back.getHeight());
        assertEquals(image.getWidth(), back.getWidth());
    }
}