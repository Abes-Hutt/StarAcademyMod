package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.item.BoosterPackItem;
import dev.architectury.hooks.item.ItemStackHooks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SelectBoosterPackC2SPacket extends ModPacket<ServerPlayNetworkHandler> {

    public static final Id<SelectBoosterPackC2SPacket> ID = new Id<>(StarAcademyMod.id("select_booster_pack_c2s"));

    private final List<Integer> selected;

    public SelectBoosterPackC2SPacket() {
        this.selected = new ArrayList<>();
    }

    public SelectBoosterPackC2SPacket(Collection<Integer> selected) {
        this.selected = new ArrayList<>(selected);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void onReceive(ServerPlayNetworkHandler listener) {
        ServerPlayerEntity player = listener.getPlayer();
        ItemStack stack = player.getStackInHand(player.getActiveHand());

        if(stack.getItem() instanceof BoosterPackItem && stack.contains(DataComponentTypes.CONTAINER)) {
            ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
            List<ItemStack> stacks = new ArrayList<>();
            List<ItemStack> toGive = new ArrayList<>();
            container.iterateNonEmpty().forEach(stacks::add);

            for(int index : this.selected) {
                if(index >= 0 && index < stacks.size()) {
                    toGive.add(stacks.get(index));
                    stacks.set(index, ItemStack.EMPTY);
                }
            }

            stacks.removeIf(ItemStack::isEmpty);
            stack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(stacks));

            if(stacks.stream().allMatch(ItemStack::isEmpty)) {
                stack.setCount(0);
            }

            for(ItemStack giving : toGive) {
                ItemStackHooks.giveItem(player, giving);
            }
        }
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.INT_SEGMENTED_3.writeBits(this.selected.size(), buffer);

        this.selected.forEach(index -> {
            Adapters.INT_SEGMENTED_3.writeBits(index, buffer);
        });
    }

    @Override
    public void readBits(BitBuffer buffer) {
        this.selected.clear();
        int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

        for(int i = 0; i < size; i++) {
            this.selected.add(Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow());
        }
    }

}

