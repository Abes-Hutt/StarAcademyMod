package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.world.data.ArmorDisplayData;
import net.minecraft.item.ItemStack;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.slot.ArmorSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ToggleArmorDisplayC2SPacket extends ModPacket<ServerPlayNetworkHandler> {

    public static final Id<ToggleArmorDisplayC2SPacket> ID = new Id<>(StarAcademyMod.id("toggle_armor_display_c2s"));

    private int id;

    public ToggleArmorDisplayC2SPacket() {

    }

    public ToggleArmorDisplayC2SPacket(int id) {
        this.id = id;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void onReceive(ServerPlayNetworkHandler listener) {
        if(listener.getPlayer().currentScreenHandler != null) {
            try {
                Slot slot = listener.getPlayer().currentScreenHandler.getSlot(this.id);

                if(slot instanceof ArmorSlot armorSlot) {
                    if(armorSlot.entity != listener.getPlayer()) {
                        return;
                    }

                    ArmorDisplayData data = ModWorldData.ARMOR_DISPLAY.getGlobal(listener.getPlayer().getWorld());
                    data.toggle(listener.getPlayer().getUuid(), armorSlot.equipmentSlot);
                }

                listener.checkForSpam();
            } catch(Exception ignored) {

            }
        }
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.INT_SEGMENTED_7.writeBits(this.id, buffer);
    }

    @Override
    public void readBits(BitBuffer buffer) {
        this.id = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
    }

}
