package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import com.cobblemon.mod.common.client.keybind.keybinds.SummaryBinding;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BroadcastItemC2SPacket extends ModPacket<ServerPlayNetworkHandler> {

    public static final Id<BroadcastItemC2SPacket> ID = new Id<>(StarAcademyMod.id("broadcast_item_c2s"));

    private int id;

    public BroadcastItemC2SPacket() {

    }

    public BroadcastItemC2SPacket(int id) {
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
                ItemStack stack = slot.getStack();
                if(stack.isEmpty()) return;

                MutableText text = Text.empty();
                text.append(listener.getPlayer().getDisplayName());
                text.append(Text.literal(" shared ").formatted(Formatting.GRAY));
                text.append(stack.toHoverableText());
                text.append(Text.literal(".").formatted(Formatting.GRAY));

                MinecraftServer server = listener.getPlayer().getServer();
                server.logChatMessage(text, MessageType.params(MessageType.CHAT, listener.getPlayer()), null);

                for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    player.sendMessage(text);
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
