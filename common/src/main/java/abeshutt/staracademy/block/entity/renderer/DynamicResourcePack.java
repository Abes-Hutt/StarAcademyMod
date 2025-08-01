package abeshutt.staracademy.block.entity.renderer;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.util.ClientScheduler;
import abeshutt.staracademy.util.ColorBlender;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DynamicResourcePack extends AbstractFileResourcePack {

    private final ZipFileWrapper zipFile;
    private final String overlay;

    public DynamicResourcePack(ResourcePackInfo info, ZipFileWrapper zipFile, String overlay) {
        super(info);
        this.zipFile = zipFile;
        this.overlay = overlay;
    }

    public static void open(ResourceType type, ResourcePackSource source, TriConsumer<Path, ResourcePackProfile.PackFactory, ResourcePackInfo> callback) {
        try {
            Path root = MinecraftClient.getInstance().runDirectory.toPath().toRealPath();
            Path cache = root.resolve("cache").toRealPath();
            Path resources = cache.resolve(type.getDirectory() + ".zip").toRealPath();

            ResourcePackInfo info = new ResourcePackInfo("Academy Runtime",
                    Text.literal("Academy Runtime"), source, Optional.empty()) {
                @Override
                public Text title() {
                    double time = 0.0D;

                    if(Platform.getEnv() == EnvType.CLIENT) {
                        time = ClientScheduler.getTick(MinecraftClient.getInstance().getRenderTickCounter()
                                .getTickDelta(true));
                    }

                    return styleText("Academy Runtime Pack", time, 10.0F);
                }
            };

            callback.accept(resources, new ZipBackedFactory(resources), info);
        } catch(IOException e) {
            StarAcademyMod.LOGGER.error("Failed to open cached %s.".formatted(type.name()), e);
        }
    }

    private static Text styleText(String string, double time, float offset) {
        MutableText text = Text.empty();
        int count = 0;

        for(int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            text = text.append(Text.literal(String.valueOf(c))
                    .setStyle(Style.EMPTY.withColor(getColor(time + count * offset))));
            if(c != ' ') count++;
        }

        return text;
    }

    public static Integer getColor(double time) {
        ColorBlender blender = new ColorBlender(1.0F)
                .add(0xE3A335, 60.0F)
                .add(0xB76D32, 60.0F);
        return blender.getColor(time);
    }

    private static String toPath(ResourceType type, Identifier id) {
        return String.format(Locale.ROOT, "%s/%s/%s", type.getDirectory(), id.getNamespace(), id.getPath());
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> openRoot(String... segments) {
        return this.openFile(String.join("/", segments));
    }

    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        return this.openFile(toPath(type, id));
    }

    private String appendOverlayPrefix(String path) {
        return this.overlay.isEmpty() ? path : this.overlay + "/" + path;
    }

    @Nullable
    private InputSupplier<InputStream> openFile(String path) {
        ZipFile zipFile = this.zipFile.open();

        if(zipFile == null) {
            return null;
        }

        ZipEntry zipEntry = zipFile.getEntry(this.appendOverlayPrefix(path));
        return zipEntry == null ? null : InputSupplier.create(zipFile, zipEntry);
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        ZipFile zipFile = this.zipFile.open();

        if(zipFile == null) {
            return Set.of();
        }

        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        Set<String> namespaces = new HashSet<>();
        String prefix = this.appendOverlayPrefix(type.getDirectory() + "/");

        while(enumeration.hasMoreElements()) {
            ZipEntry zipEntry = enumeration.nextElement();
            String name = zipEntry.getName();
            String namespace = getNamespace(prefix, name);

            if(!namespace.isEmpty()) {
                if(Identifier.isNamespaceValid(namespace)) {
                    namespaces.add(namespace);
                } else {
                    StarAcademyMod.LOGGER.warn("Non [a-z0-9_.-] character in namespace {} in pack {}, ignoring.",
                            namespace, this.zipFile.file);
                }
            }
        }

        return namespaces;
    }

    public static String getNamespace(String prefix, String entryName) {
        if(!entryName.startsWith(prefix)) {
            return "";
        }

        int length = prefix.length();
        int divider = entryName.indexOf('/', length);
        return divider == -1 ? entryName.substring(length) : entryName.substring(length, divider);
    }

    @Override
    public void close() {
        this.zipFile.close();
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
        ZipFile zipFile = this.zipFile.open();

        if(zipFile != null) {
            Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
            String header = this.appendOverlayPrefix(type.getDirectory() + "/" + namespace + "/");
            String path = header + prefix + "/";

            while(enumeration.hasMoreElements()) {
                ZipEntry zipEntry = enumeration.nextElement();

                if(zipEntry.isDirectory()) {
                    continue;
                }

                String name = zipEntry.getName();

                if(name.startsWith(path)) {
                    String stripped = name.substring(header.length());
                    Identifier identifier = Identifier.tryParse(namespace, stripped);

                    if(identifier != null) {
                        consumer.accept(identifier, InputSupplier.create(zipFile, zipEntry));
                    } else {
                        StarAcademyMod.LOGGER.warn("Invalid path in pack: {}:{}, ignoring.", namespace, stripped);
                    }
                }
            }
        }
    }

    public static class ZipBackedFactory implements ResourcePackProfile.PackFactory {
        private final File file;

        public ZipBackedFactory(Path path) {
            this(path.toFile());
        }

        public ZipBackedFactory(File file) {
            this.file = file;
        }

        @Override
        public ResourcePack open(ResourcePackInfo info) {
            ZipFileWrapper zipFileWrapper = new ZipFileWrapper(this.file);
            return new DynamicResourcePack(info, zipFileWrapper, "");
        }

        @Override
        public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
            ZipFileWrapper zipFileWrapper = new ZipFileWrapper(this.file);
            ResourcePack resourcePack = new DynamicResourcePack(info, zipFileWrapper, "");
            List<String> overlays = metadata.overlays();

            if(overlays.isEmpty()) {
                return resourcePack;
            }

            List<ResourcePack> copy = new ArrayList<>(overlays.size());

            for(String string : overlays) {
                copy.add(new DynamicResourcePack(info, zipFileWrapper, string));
            }

            return new OverlayResourcePack(resourcePack, copy);
        }
    }

    public static class ZipFileWrapper implements AutoCloseable {
        final File file;
        private ZipFile zip;
        private boolean closed;

        public ZipFileWrapper(File file) {
            this.file = file;
        }

        @Nullable
        public ZipFile open() {
            if(this.closed) {
                return null;
            }

            if(this.zip == null) {
                try {
                    this.zip = new ZipFile(this.file);
                } catch(IOException var2) {
                    StarAcademyMod.LOGGER.error("Failed to open pack {}.", this.file, var2);
                    this.closed = true;
                    return null;
                }
            }

            return this.zip;
        }

        @Override
        public void close() {
            if(this.zip != null) {
                IOUtils.closeQuietly(this.zip);
                this.zip = null;
            }
        }

        @Override
        protected void finalize() throws Throwable {
            this.close();
            super.finalize();
        }
    }

}
