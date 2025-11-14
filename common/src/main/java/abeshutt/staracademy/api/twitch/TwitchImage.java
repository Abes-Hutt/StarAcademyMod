package abeshutt.staracademy.api.twitch;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.IJsonSerializable;
import com.google.common.hash.Hashing;
import com.google.gson.JsonElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static net.minecraft.client.texture.NativeImage.Format.RGBA;

public class TwitchImage implements IJsonSerializable<JsonElement> {

    private String url;
    private String hash;
    private Identifier id;

    public TwitchImage() {
        this("");
    }

    public TwitchImage(String url) {
        this.url = url;
        this.hash = Integer.toHexString(Hashing.murmur3_32_fixed().hashString(this.url, StandardCharsets.UTF_8).asInt() >>> 1);
        this.id = StarAcademyMod.id("twitch" + "/" + "profile_picture" + "/" + this.hash);
    }

    public String getUrl() {
        return this.url;
    }

    public String getHash() {
        return this.hash;
    }

    public Identifier getId() {
        return this.id;
    }

    public synchronized void fetch() {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        Identifier id = this.getId();

        if (minecraft.getTextureManager().getOrDefault(id, null) instanceof NativeImageBackedTexture backed && backed.getImage() != null) {
            return;
        }

        try {
            URL url = URI.create(this.url).toURL();
            BufferedImage bufferedImage = ImageIO.read(url);
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            NativeImage image = new NativeImage(RGBA, width, height, false);

            int centerX = width / 2;
            int centerY = height / 2;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int dx = x - centerX;
                    int dy = y - centerY;
                    //if (dx * dx + dy * dy <= width * width / 4) {
                        int argb = bufferedImage.getRGB(x, y);
                        int a = (argb >>> 24) & 0xFF;
                        int r = (argb >>> 16) & 0xFF;
                        int g = (argb >>> 8) & 0xFF;
                        int b = (argb) & 0xFF;
                        image.setColor(x, y, (a << 24) | (b << 16) | (g << 8) | r);
                    //} else {
                    //    image.setColor(x, y, 0);
                    //}
                }
            }

            minecraft.executeSync(() -> {
                minecraft.getTextureManager().registerTexture(id, new NativeImageBackedTexture(image));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<JsonElement> writeJson() {
        return Adapters.UTF_8.writeJson(this.url);
    }

    @Override
    public void readJson(JsonElement json) {
        this.url = Adapters.UTF_8.readJson(json).orElse("");
        this.hash = Integer.toHexString(Hashing.murmur3_32_fixed().hashString(this.url, StandardCharsets.UTF_8).asInt() >>> 1);
        this.id = StarAcademyMod.id("twitch" + "/" + "profile_picture" + "/" + this.hash);
    }

}
