package abeshutt.staracademy.client;

import abeshutt.staracademy.StarAcademyMod;
import com.google.common.hash.Hashing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    public void check(AcademyClient client) {
        Path cache;
        Integer assetsChecksum;
        Integer dataChecksum;

        try {
            Path root = MinecraftClient.getInstance().runDirectory.toPath().toRealPath();
            cache = root.resolve("codex").toRealPath();

            try {
                assetsChecksum = hash(cache.resolve(CLIENT_RESOURCES.getDirectory() + ".zip").toRealPath());
            } catch (Exception e) {
                assetsChecksum = null;
            }

            try {
                dataChecksum = hash(cache.resolve(SERVER_DATA.getDirectory() + ".zip").toRealPath());
            } catch (Exception e) {
                dataChecksum = null;
            }
        } catch (Exception e) {
            assetsChecksum = null;
            dataChecksum = null;
        }

        client.send(new CheckCodexPacket(assetsChecksum, dataChecksum));
    }

    public void receive(AcademyClient client, byte[] assets, byte[] data) {
        Path root = client.getMinecraft().runDirectory.toPath();
        if (assets != null) this.writeResources(root, CLIENT_RESOURCES, assets);
        if (data != null) this.writeResources(root, SERVER_DATA, data);
        this.complete = true;
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

    public static Integer hash(Path path) {
        try {
            return Hashing.murmur3_32_fixed().hashBytes(Files.readAllBytes(path)).asInt();
        } catch (IOException e) {
            return null;
        }
    }

}
