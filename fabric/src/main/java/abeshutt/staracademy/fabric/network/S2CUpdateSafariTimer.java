package abeshutt.staracademy.fabric.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.time.Instant;

public class S2CUpdateSafariTimer implements CustomPayload {

    public final Instant end;

    public S2CUpdateSafariTimer() {
        this.end = null;
    }

    public S2CUpdateSafariTimer(Instant end) {
        this.end = end;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static final CustomPayload.Id<S2CUpdateSafariTimer> ID = new CustomPayload.Id<>(Identifier.of("safari:update_safari_timer"));

   public static final PacketCodec<PacketByteBuf, S2CUpdateSafariTimer> CODEC = new PacketCodec<PacketByteBuf, S2CUpdateSafariTimer>() {
        @Override
        public void encode(PacketByteBuf buffer, S2CUpdateSafariTimer packet) {
            buffer.writeNullable(packet.end, (buf, value) -> {
                buf.writeLong(value.toEpochMilli());
            });
        }

        @Override
        public S2CUpdateSafariTimer decode(PacketByteBuf buffer) {
            var epochMilli = buffer.readNullable(PacketByteBuf::readLong);
            if (epochMilli == null) {
                return new S2CUpdateSafariTimer(null);
            }
            else {
                return new S2CUpdateSafariTimer(Instant.ofEpochMilli(epochMilli));
            }
        }
    };

}
