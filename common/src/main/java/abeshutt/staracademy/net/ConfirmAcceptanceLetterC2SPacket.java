package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.init.ModDataComponents;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.item.AcceptanceLetterItem;
import abeshutt.staracademy.world.data.AcademyHouse;
import abeshutt.staracademy.world.data.HouseData;
import abeshutt.staracademy.world.data.WardrobeData;
import dev.architectury.hooks.item.ItemStackHooks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

import static abeshutt.staracademy.init.ModDataComponents.*;

public class ConfirmAcceptanceLetterC2SPacket extends ModPacket<ServerPlayNetworkHandler> {

    public static final Id<ConfirmAcceptanceLetterC2SPacket> ID = new Id<>(StarAcademyMod.id("confirm_acceptance_letter_c2s"));

    public ConfirmAcceptanceLetterC2SPacket() {

    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void onReceive(ServerPlayNetworkHandler listener) {
        ServerPlayerEntity player = listener.getPlayer();
        ItemStack stack = player.getStackInHand(player.getActiveHand());

        if(stack.getItem() instanceof AcceptanceLetterItem && player.getUuid()
                .equals(stack.getOrDefault(ACCEPTANCE_LETTER_OWNER.get(), null))) {
            HouseData data = ModWorldData.HOUSE.getGlobal(player.getWorld());
            AcademyHouse house = data.get(stack.getOrDefault(ACCEPTANCE_LETTER_HOUSE.get(), null)).orElse(null);
            if(house == null) return;
            data.getFor(player.getUuid()).ifPresent(other -> {
                other.removePlayer(player.getUuid());
            });

            stack.set(ACCEPTANCE_LETTER_ENROLLED.get(), true);
            player.setStackInHand(player.getActiveHand(), stack);

            for(ServerPlayerEntity other : player.getServer().getPlayerManager().getPlayerList()) {
                other.sendMessage(Text.empty()
                        .append(player.getDisplayName())
                        .append(Text.literal(" has joined house ").formatted(Formatting.GRAY))
                        .append(Text.literal(house.getName()).setStyle(Style.EMPTY.withColor(house.getColor())))
                        .append(Text.literal(".").formatted(Formatting.GRAY)));
            }

            house.addPlayer(player.getUuid());
        }
    }

    @Override
    public void writeBits(BitBuffer buffer) {

    }

    @Override
    public void readBits(BitBuffer buffer) {

    }

}
