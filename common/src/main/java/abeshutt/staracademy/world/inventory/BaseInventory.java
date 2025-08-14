package abeshutt.staracademy.world.inventory;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.INbtSerializable;
import abeshutt.staracademy.util.ItemUtils;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.util.collection.DefaultedList;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BaseInventory implements Inventory, RecipeInputProvider, INbtSerializable<NbtCompound> {

    private int size;
    private DefaultedList<ItemStack> stacks;
    private List<InventoryChangedListener> listeners;
    private boolean dirty;

    public BaseInventory(int size) {
        this.size = size;
        this.stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
    }

    public BaseInventory(ItemStack... items) {
        this.size = items.length;
        this.stacks = DefaultedList.copyOf(ItemStack.EMPTY, items);
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void addListener(InventoryChangedListener listener) {
        if(this.listeners == null) {
            this.listeners = Lists.newArrayList();
        }

        this.listeners.add(listener);
    }

    public void removeListener(InventoryChangedListener listener) {
        if(this.listeners != null) {
            this.listeners.remove(listener);
        }
    }

    @Override
    public ItemStack getStack(int slot) {
        return slot >= 0 && slot < this.stacks.size() ? this.stacks.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack(this.stacks, slot, amount);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }

        return itemStack;
    }

    public ItemStack removeItem(Item item, int count) {
        ItemStack itemStack = new ItemStack(item, 0);

        for(int i = this.size - 1; i >= 0; --i) {
            ItemStack itemStack2 = this.getStack(i);
            if (itemStack2.getItem().equals(item)) {
                int j = count - itemStack.getCount();
                ItemStack itemStack3 = itemStack2.split(j);
                itemStack.increment(itemStack3.getCount());
                if (itemStack.getCount() == count) {
                    break;
                }
            }
        }

        if (!itemStack.isEmpty()) {
            this.markDirty();
        }

        return itemStack;
    }

    public ItemStack addStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            ItemStack itemStack = stack.copy();
            this.addToExistingSlot(itemStack);
            if (itemStack.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                this.addToNewSlot(itemStack);
                return itemStack.isEmpty() ? ItemStack.EMPTY : itemStack;
            }
        }
    }

    public boolean canInsert(ItemStack stack) {
        boolean bl = false;
        Iterator var3 = this.stacks.iterator();

        while(var3.hasNext()) {
            ItemStack itemStack = (ItemStack)var3.next();
            if (itemStack.isEmpty() || ItemStack.areItemsAndComponentsEqual(itemStack, stack) && itemStack.getCount() < itemStack.getMaxCount()) {
                bl = true;
                break;
            }
        }

        return bl;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack itemStack = this.stacks.get(slot);
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.stacks.set(slot, ItemStack.EMPTY);
            return itemStack;
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.stacks.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }

        this.markDirty();
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        Iterator var1 = this.stacks.iterator();

        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack = (ItemStack)var1.next();
        } while(itemStack.isEmpty());

        return false;
    }

    @Override
    public void markDirty() {
        this.setDirty(true);

        if(this.listeners != null) {
            for(InventoryChangedListener listener : this.listeners) {
                listener.onInventoryChanged(this);
            }
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.stacks.clear();
        this.markDirty();
    }

    @Override
    public void provideRecipeInputs(RecipeMatcher finder) {
        Iterator var2 = this.stacks.iterator();

        while(var2.hasNext()) {
            ItemStack itemStack = (ItemStack)var2.next();
            finder.addInput(itemStack);
        }
    }

    @Override
    public String toString() {
        return ((List)this.stacks.stream().filter((stack) -> {
            return !stack.isEmpty();
        }).collect(Collectors.toList())).toString();
    }

    private void addToNewSlot(ItemStack stack) {
        for(int i = 0; i < this.size; ++i) {
            ItemStack itemStack = this.getStack(i);
            if (itemStack.isEmpty()) {
                this.setStack(i, stack.copyAndEmpty());
                return;
            }
        }

    }

    private void addToExistingSlot(ItemStack stack) {
        for(int i = 0; i < this.size; ++i) {
            ItemStack itemStack = this.getStack(i);
            if (ItemStack.areItemsAndComponentsEqual(itemStack, stack)) {
                this.transfer(stack, itemStack);
                if (stack.isEmpty()) {
                    return;
                }
            }
        }

    }

    private void transfer(ItemStack source, ItemStack target) {
        int i = Math.min(this.getMaxCountPerStack(), target.getMaxCount());
        int j = Math.min(source.getCount(), i - target.getCount());
        if (j > 0) {
            target.increment(j);
            source.decrement(j);
            this.markDirty();
        }

    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            Adapters.INT.writeNbt(this.size()).ifPresent(tag -> nbt.put("size", tag));
            NbtList items = new NbtList();

            for(int i = 0; i < this.size; i++) {
                ItemStack stack = this.stacks.get(i);
                if(stack.isEmpty()) continue;

                NbtCompound entry = new NbtCompound();
                Adapters.INT.writeNbt(i).ifPresent(tag -> entry.put("index", tag));
                Adapters.ITEM.writeNbt(stack.getItem()).ifPresent(tag -> entry.put("item", tag));
                Adapters.INT.writeNbt(stack.getCount()).ifPresent(tag -> entry.put("count", tag));
                Adapters.COMPOUND_NBT.writeNbt(ItemUtils.getNbt(stack)).ifPresent(tag -> entry.put("nbt", tag));
                items.add(entry);
            }

            nbt.put("items", items);
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.size = Adapters.INT.readNbt(nbt.get("size")).orElse(0);
        this.stacks = DefaultedList.ofSize(this.size, ItemStack.EMPTY);

        NbtList items = nbt.getList("items", NbtElement.COMPOUND_TYPE);

        for(int i = 0; i < items.size(); i++) {
            NbtCompound entry = items.getCompound(i);
            int index = Adapters.INT.readNbt(entry.get("index")).orElse(-1);
            Item item = Adapters.ITEM.readNbt(entry.get("item")).orElse(Items.AIR);
            int count = Adapters.INT.readNbt(entry.get("count")).orElse(1);
            NbtCompound compound = Adapters.COMPOUND_NBT.readNbt(entry.get("nbt")).orElse(null);

            ItemStack stack = new ItemStack(item, count);

            if(compound != null) {
                ItemUtils.setNbt(stack, compound);
            }

            if(index >= 0 && index < this.size) {
                this.stacks.set(index, stack);
            }
        }

        this.markDirty();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }

        if(o instanceof BaseInventory other) {
            return ItemStack.stacksEqual(this.stacks, other.stacks);
        }

        return false;
    }

}
