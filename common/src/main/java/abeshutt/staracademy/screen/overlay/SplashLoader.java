package abeshutt.staracademy.screen.overlay;

import abeshutt.staracademy.StarAcademyMod;
import com.madgag.gif.fmsware.GifDecoder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static net.minecraft.client.texture.NativeImage.Format.RGBA;

public class SplashLoader {

    public static final Map<Identifier, Double> FRAMES = new LinkedHashMap<>();

    public static Optional<Identifier> getFrame(double time) {
        double totalTime = 0.0d;

        for (double frameTime : FRAMES.values()) {
            totalTime += frameTime;
        }

        if (totalTime <= 0.0d) {
            return Optional.empty();
        }

        double progress = time % totalTime;

        for (Map.Entry<Identifier, Double> entry : FRAMES.entrySet()) {
            if (progress >= entry.getValue()) {
                progress -= entry.getValue();
            } else {
                return Optional.of(entry.getKey());
            }
        }

        return Optional.empty();
    }

    public static void load(MinecraftClient client, BufferedImage bufferedImage, Identifier id) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        NativeImage image = new NativeImage(RGBA, width, height, false);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = bufferedImage.getRGB(x, y);
                int a = (argb >>> 24) & 0xFF;
                int r = (argb >>> 16) & 0xFF;
                int g = (argb >>> 8) & 0xFF;
                int b = (argb) & 0xFF;
                image.setColor(x, y, (a << 24) | (b << 16) | (g << 8) | r);
            }
        }

        client.executeSync(() -> {
            client.getTextureManager().registerTexture(id, new NativeImageBackedTexture(image));
        });
    }

    public static void load(MinecraftClient client) {
        try {
            FileInputStream in = new FileInputStream(Path.of("config", StarAcademyMod.ID, "assets", "splash.gif").toFile());

            GifDecoder decoder = new GifDecoder();
            decoder.read(in);

            for (int i = 0; i < decoder.getFrameCount(); i++) {
                BufferedImage bufferedImage = decoder.getFrame(i);
                int delay = decoder.getDelay(i);
                Identifier id = StarAcademyMod.id("splash/frame/" + i);
                load(client, bufferedImage, id);
                FRAMES.put(id, delay / 50.0d);
            }

            load(client, ImageIO.read(Path.of("config", StarAcademyMod.ID, "assets", "logo.png").toFile()),
                    StarAcademyMod.id("splash/logo"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
