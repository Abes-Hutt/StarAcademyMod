package abeshutt.staracademy.screen.overlay;

import abeshutt.staracademy.StarAcademyMod;
import com.madgag.gif.fmsware.GifDecoder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
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

    public static void load(MinecraftClient client, BufferedImage bufferedImage, Identifier id, NativeImage.Format format) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        NativeImage image = new NativeImage(format, width, height, false);
        int[] pixels;

        if (bufferedImage.getRaster().getDataBuffer() instanceof DataBufferInt buffer) {
            pixels = buffer.getData(); // Already ARGB
        } else {
            pixels = new int[width * height];
            bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);
        }

        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++, index++) {
                int argb = pixels[index];
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
        long start = System.nanoTime();

        try {
            FileInputStream in = new FileInputStream(Path.of("config", StarAcademyMod.ID, "assets", "splash.gif").toFile());

            GifDecoder decoder = new GifDecoder();
            decoder.read(in);
            StarAcademyMod.LOGGER.warn("Decoding splash frames took {} seconds.", (System.nanoTime() - start) / 1_000_000_000.0D);

            for (int i = 0; i < decoder.getFrameCount(); i++) {
                BufferedImage bufferedImage = decoder.getFrame(i);
                int delay = decoder.getDelay(i);
                Identifier id = StarAcademyMod.id("splash/frame/" + i);
                load(client, bufferedImage, id, RGBA);
                FRAMES.put(id, delay / 50.0d);
            }

            load(client, ImageIO.read(Path.of("config", StarAcademyMod.ID, "assets", "logo.png").toFile()),
                    StarAcademyMod.id("splash/logo"), RGBA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
