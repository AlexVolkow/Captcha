package services.captcha;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CaptchaGenerator implements CaptchaService.CaptchaCallback {
    private final static Logger logger = LogManager.getLogger(CaptchaGenerator.class.getName());

    private static final Color[] colors = {Color.red, Color.black, Color.blue};
    protected static final String BACKGROUND_PATH = "resources/background.jpg";

    private Map<Captcha, RenderedImage> images = new ConcurrentHashMap<>();

    public RenderedImage getImage(Captcha captcha) {
        return images.computeIfAbsent(captcha, this::renderImage);
    }

    private RenderedImage renderImage(Captcha captcha) {
        String value = captcha.getAnswer();

        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("not null or empty value expected");
        }

        BufferedImage image;
        try {
            image = ImageIO.read(new File(BACKGROUND_PATH));
        } catch (IOException e) {
            logger.error("An error occurs during reading file " + BACKGROUND_PATH);
            return null;
        }

        Graphics g = image.getGraphics();
        g.setFont(g.getFont().deriveFont(30f));
        char[] chars = value.toCharArray();
        int x = 5;
        int y = 50;
        for (char aChar : chars) {
            x = x + 30;
            g.setColor(colors[(int) (Math.random() * colors.length)]);
            g.drawString(String.valueOf(aChar), x, y);
        }
        g.dispose();

        logger.info("Create image for captcha " + captcha.getToken());
        return image;
    }

    @Override
    public void removeCaptcha(Captcha captcha) {
        images.remove(captcha);
    }
}
