package abeshutt.staracademy.live;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.live.api.dto.Hash;
import com.google.common.hash.Hashing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static net.minecraft.resource.ResourceType.CLIENT_RESOURCES;
import static net.minecraft.resource.ResourceType.SERVER_DATA;

public class CodexManager {

    private boolean complete;

    public CodexManager() {
        this.complete = false;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Optional<Hash> getCodexHash(Hash.Algorithm algorithm) {
        try {
            Path path = MinecraftClient.getInstance().runDirectory.toPath().toRealPath();
            path = path.resolve("codex").toRealPath();
            path = path.resolve(CLIENT_RESOURCES.getDirectory() + ".zip").toRealPath();
            return this.hash(path, algorithm);
        } catch (Exception ignored) { }

        return Optional.empty();
    }

    public void receive(AcademyClient client, byte[] assets) {
        if (this.complete) {
            return;
        }

        Path root = client.getMinecraft().runDirectory.toPath();
        if (assets != null) this.writeResources(root, CLIENT_RESOURCES, assets);
        this.setComplete(true);
    }

    public void writeResources(Path root, ResourceType type, byte[] data) {
        try {
            root = root.toRealPath();
        } catch (Exception e) {
            StarAcademyMod.LOGGER.error("Failed to write {} to cache. Root doesn't exist.", type.getDirectory(), e);
            return;
        }

        Path cache;

        try {
            cache = root.resolve("codex").toRealPath();
        } catch (Exception e) {
            try {
                Files.createDirectory(cache = root.resolve("codex"));
            } catch (IOException ex) {
                return;
            }
        }

        Path resources;

        try {
            resources = cache.resolve(type.getDirectory() + ".zip").toRealPath();
        } catch (Exception e) {
            resources = cache.resolve(type.getDirectory() + ".zip");
        }

        try {
            Files.write(resources, data);
        } catch (IOException e) {
            StarAcademyMod.LOGGER.error("Failed to write {} to cache.", type.getDirectory(), e);
        }
    }

    public Optional<Hash> hash(Path path, Hash.Algorithm hashing) {
        try {
            return Optional.ofNullable(hashing.hash(Files.readAllBytes(path)))
                    .map(bytes -> Hash.of(hashing, bytes));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

}
