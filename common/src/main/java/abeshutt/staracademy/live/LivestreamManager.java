package abeshutt.staracademy.live;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.live.api.dto.LivestreamDisplay;
import abeshutt.staracademy.util.threading.ThreadPool;
import cn.leolezury.eternalstarlight.common.entity.living.animal.Ent;
import com.google.common.hash.Hashing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.texture.NativeImage.Format.RGBA;

public class LivestreamManager {

    private static final ThreadPool IMAGE_DOWNLOADER = new ThreadPool(Runtime.getRuntime().availableProcessors());

    private final List<Entry> streams;
    private boolean dirty;
    private int iteration;

    public LivestreamManager() {
        this.streams = new ArrayList<>();
    }

    public List<Entry> getStreams() {
        return this.streams;
    }

    public int getIteration() {
        return this.iteration;
    }

    public void update(List<LivestreamDisplay> streams) {
        this.streams.clear();
        this.streams.addAll(streams.stream().map(Entry::new).toList());
        this.iteration++;
        this.dirty = true;
    }

    public void tick(AcademyClient client) {
        if (this.dirty) {
            for (Entry stream : this.streams) {
                IMAGE_DOWNLOADER.execute(stream::fetch);
            }

            this.dirty = false;
        }
    }

    public static class Entry {
        private final LivestreamDisplay stream;
        private final String profilePictureHash;
        private final Identifier profilePictureTexture;

        public Entry(LivestreamDisplay stream) {
            this.stream = stream;
            this.profilePictureHash = Integer.toHexString(Hashing.murmur3_32_fixed().hashString(
                    stream.getProfilePictureUrl(), StandardCharsets.UTF_8).asInt() >>> 1);
            this.profilePictureTexture = StarAcademyMod.id("livestream" + "/" + "profile_picture" +
                    "/" + this.profilePictureHash);
        }

        public LivestreamDisplay getStream() {
            return this.stream;
        }

        public String getProfilePictureHash() {
            return this.profilePictureHash;
        }

        public Identifier getProfilePictureTexture() {
            return this.profilePictureTexture;
        }

        public void fetch() {
            MinecraftClient minecraft = MinecraftClient.getInstance();

            if (minecraft.getTextureManager().getOrDefault(this.profilePictureTexture, null)
                    instanceof NativeImageBackedTexture backed && backed.getImage() != null) {
                return;
            }

            try {
                URL url = URI.create(this.stream.getProfilePictureUrl()).toURL();
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
                    minecraft.getTextureManager().registerTexture(this.profilePictureTexture,
                            new NativeImageBackedTexture(image));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
