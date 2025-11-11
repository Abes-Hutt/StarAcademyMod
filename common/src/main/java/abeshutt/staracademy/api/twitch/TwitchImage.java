package abeshutt.staracademy.api.twitch;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.IJsonSerializable;
import abeshutt.staracademy.screen.widget.ImageTexture;
import com.google.common.hash.Hashing;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static net.minecraft.client.texture.NativeImage.Format.RGBA;

public class TwitchImage implements IJsonSerializable<JsonElement> {

    private String url;

    private int width;
    private int height;
    private String hash;

    public TwitchImage() {
        this("");
    }

    public TwitchImage(String url) {
        this.url = url;
        this.width = 0;
        this.height = 0;
        this.hash = "null";
    }

    public String getUrl() {
        return this.url;
    }

    public Identifier getId() {
        return StarAcademyMod.id("twitch" + "/" + "profile_picture" + "/" + this.hash);
    }

    public ImageTexture asTexture() {
        return new ImageTexture(this.getId(), this.width, this.height);
    }

    public synchronized void fetch() {
        this.hash = Integer.toHexString(Hashing.murmur3_32_fixed()
                .hashString(this.url, StandardCharsets.UTF_8).asInt() >>> 1);

        MinecraftClient minecraft = MinecraftClient.getInstance();
        Identifier id = this.getId();

        if (minecraft.getTextureManager().getOrDefault(id, null) != null) {
            return;
        }

        NativeImage image = new NativeImage(RGBA, this.width, this.height, false);

        try {
            URL url = new URL(this.url);
            BufferedImage bufferedImage = ImageIO.read(url);

            for (int x = 0; x < this.width; x++) {
                for (int y = 0; y < this.height; y++) {
                    int argb = bufferedImage.getRGB(x, y);
                    int a = (argb >>> 24) & 0xFF;
                    int r = (argb >>> 16) & 0xFF;
                    int g = (argb >>> 8) & 0xFF;
                    int b = (argb) & 0xFF;
                    image.setColor(x, y, (a << 24) | (r << 16) | (g << 8) | b);
                }
            }

            minecraft.getTextureManager().registerTexture(id, new NativeImageBackedTexture(image));
        } catch (Exception e) {
            e.printStackTrace();
            this.width = 0;
            this.height = 0;
            this.hash = "null";
        }
    }

    @Override
    public Optional<JsonElement> writeJson() {
        return Adapters.UTF_8.writeJson(this.url);
    }

    @Override
    public void readJson(JsonElement json) {
        this.width = 0;
        this.height = 0;
        this.hash = "null";

        if (json instanceof JsonPrimitive primitive && primitive.isString()) {
            this.url = primitive.toString();
        } else {
            this.url = "";
        }
    }

}
