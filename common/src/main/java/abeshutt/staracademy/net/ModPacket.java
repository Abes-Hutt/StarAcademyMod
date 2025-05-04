package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.bit.ArrayBitBuffer;
import abeshutt.staracademy.data.serializable.IBitSerializable;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.thread.ThreadExecutor;

public abstract class ModPacket<T extends PacketListener> implements CustomPayload, IBitSerializable {

    public abstract void onReceive(T listener);

    public final void write(PacketByteBuf buf) {
        ArrayBitBuffer buffer = ArrayBitBuffer.empty();
        this.writeBits(buffer);
        buf.writeLongArray(buffer.toLongArray());
    }

    public final void read(PacketByteBuf buf) {
        ArrayBitBuffer buffer = ArrayBitBuffer.backing(buf.readLongArray(), 0);
        this.readBits(buffer);
    }

    public void apply(T listener) {
        ThreadExecutor<?> engine = switch(Platform.getEnvironment()) {
            case CLIENT -> this.getClientEngine(listener);
            case SERVER -> this.getServerEngine(listener);
        };

        if(engine == null || engine.isOnThread()) {
            this.onReceive(listener);
            return;
        }

        engine.executeSync(() -> {
            if(listener.isConnectionOpen()) {
                try {
                    this.apply(listener);
                } catch(Exception exception) {
                    StarAcademyMod.LOGGER.error("Failed to handle packet {}, suppressing error", this, exception);
                }
            } else {
                StarAcademyMod.LOGGER.debug("Ignoring packet due to disconnection: {}", this);
            }
        });
    }

    @Environment(EnvType.CLIENT)
    public ThreadExecutor<?> getClientEngine(T listener) {
        if(listener instanceof ServerPlayNetworkHandler handler) {
            return handler.player.getServer();
        } else if(listener instanceof ClientPlayNetworkHandler) {
            return MinecraftClient.getInstance();
        }

        StarAcademyMod.LOGGER.error("Failed to handle packet {}, engine {} is unknown", this, listener);
        return null;
    }

    @Environment(EnvType.SERVER)
    public ThreadExecutor<?> getServerEngine(T listener) {
        if(listener instanceof ServerPlayNetworkHandler handler) {
            return handler.player.getServer();
        }

        StarAcademyMod.LOGGER.error("Failed to handle packet {}, engine {} is unknown", this, listener);
        return null;
    }

}
