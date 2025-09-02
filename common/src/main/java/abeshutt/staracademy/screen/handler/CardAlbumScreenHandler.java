package abeshutt.staracademy.screen.handler;

import abeshutt.staracademy.data.component.CardAlbumInventory;
import abeshutt.staracademy.init.ModDataComponents;
import abeshutt.staracademy.init.ModScreenHandlers;
import abeshutt.staracademy.item.CardItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class CardAlbumScreenHandler extends ScreenHandler {

    private final PlayerEntity player;
    private final CardAlbumInventory inventory;
    private final ItemStack stack;
    private final int slot;
    private final InventoryChangedListener listener;

    public CardAlbumScreenHandler(int id, PlayerInventory inventory, PacketByteBuf buf) {
        this(id, inventory.player, ItemStack.EMPTY, new CardAlbumInventory());
    }

    public CardAlbumScreenHandler(int syncId, PlayerEntity player, ItemStack stack, CardAlbumInventory inventory) {
        super(ModScreenHandlers.CARD_ALBUM.get(), syncId);
        this.player = player;
        this.inventory = inventory;
        this.stack = stack;
        this.slot = this.player.getInventory().selectedSlot;

        this.listener = sender -> {
            this.player.getInventory().getStack(this.slot).set(ModDataComponents.CARD_ALBUM_CONTAINER.get(), inventory.copy());
        };

        inventory.onOpen(player);
        this.inventory.addListener(this.listener);

        int i = 18;

        int j;
        int k;

        for(j = 0; j < 2; ++j) {
            for(k = 0; k < 6; ++k) {
                this.addSlot(new CardSlot(inventory, k + j * 6, 8 + 6 + k * 37 + (k >= 3 ? 11 : 0), 18 + 7 + j * 71));
            }
        }

        for(j = 0; j < 3; ++j) {
            for(k = 0; k < 9; ++k) {
                this.addSlot(new Slot(this.player.getInventory(), k + j * 9 + 9, 8 + 40 + k * 18, 103 + 48 + j * 18 + i) {
                    @Override
                    public boolean canTakeItems(PlayerEntity playerEntity) {
                        return this.getIndex() != CardAlbumScreenHandler.this.slot;
                    }

                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return this.getIndex() != CardAlbumScreenHandler.this.slot && stack != CardAlbumScreenHandler.this.stack;
                    }
                });
            }
        }

        for(j = 0; j < 9; ++j) {
            this.addSlot(new Slot(this.player.getInventory(), j, 8 + 40 + j * 18, 161 + 48 + i) {
                @Override
                public boolean canTakeItems(PlayerEntity playerEntity) {
                    return this.getIndex() != CardAlbumScreenHandler.this.slot;
                }

                @Override
                public boolean canInsert(ItemStack stack) {
                    return this.getIndex() != CardAlbumScreenHandler.this.slot && stack != CardAlbumScreenHandler.this.stack;
                }
            });
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);

        if(slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();

            if(slot < 6 * 2) {
                if (!this.insertItem(itemStack2, 6 * 2, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, 6 * 2, false)) {
                return ItemStack.EMPTY;
            }

            if(itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }

        return itemStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
        this.inventory.removeListener(this.listener);
    }

    public static class CardSlot extends Slot {
        public CardSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canBeHighlighted() {
            return false;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.getItem() instanceof CardItem;
        }
    }

}
