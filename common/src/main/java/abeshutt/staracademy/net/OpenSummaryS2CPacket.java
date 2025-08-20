package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.bit.BitBuffer;
import com.cobblemon.mod.common.client.keybind.keybinds.SummaryBinding;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.CustomPayload;

public class OpenSummaryS2CPacket extends ModPacket<ClientPlayNetworkHandler> {

    public static final Id<OpenSummaryS2CPacket> ID = new Id<>(StarAcademyMod.id("open_summary_s2c"));

    public OpenSummaryS2CPacket() {

    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void onReceive(ClientPlayNetworkHandler listener) {
        SummaryBinding.INSTANCE.onPress();
    }

    @Override
    public void writeBits(BitBuffer buffer) {

    }

    @Override
    public void readBits(BitBuffer buffer) {

    }

}
