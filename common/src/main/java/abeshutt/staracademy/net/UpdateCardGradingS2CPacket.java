package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.card.CardGradingData;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UpdateCardGradingS2CPacket extends ModPacket<ClientPlayNetworkHandler> {

    public static final Id<UpdateCardGradingS2CPacket> ID = new Id<>(StarAcademyMod.id("update_card_grading_s2c"));

    private CardGradingData data;

    public UpdateCardGradingS2CPacket() {

    }

    public UpdateCardGradingS2CPacket(@Nullable CardGradingData data) {
        this.data = data;
    }
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void onReceive(ClientPlayNetworkHandler listener) {
        CardGradingData.CLIENT = this.data;
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.BOOLEAN.writeBits(data == null, buffer);

        if(data != null) {
            Adapters.LONG.writeBits(data.getCompletionTime().toEpochMilli(), buffer);
            Adapters.ITEM_STACK.writeBits(data.getStack(), buffer);
        }
    }

    @Override
    public void readBits(BitBuffer buffer) {
        if(Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
            this.data = null;
        } else {
            var completionTime = Instant.ofEpochMilli(Adapters.LONG.readBits(buffer).orElseThrow());
            var stack = Adapters.ITEM_STACK.readBits(buffer).orElseThrow();
            this.data = new CardGradingData(completionTime, stack);
        }
    }

}
