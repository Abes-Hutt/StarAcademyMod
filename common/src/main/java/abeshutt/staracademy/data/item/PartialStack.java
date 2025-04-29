package abeshutt.staracademy.data.item;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.nbt.PartialCompoundNbt;
import abeshutt.staracademy.util.ItemUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.Optional;

public class PartialStack implements ItemPlacement<PartialStack> {

    protected PartialItem item;
    protected PartialCompoundNbt nbt;

    protected PartialStack(PartialItem item, PartialCompoundNbt nbt) {
        this.item = item;
        this.nbt = nbt;
    }

    public static PartialStack of(PartialItem item, PartialCompoundNbt nbt) {
        return new PartialStack(item, nbt);
    }

    public static PartialStack of(ItemStack stack) {
        return new PartialStack(PartialItem.of(stack), PartialCompoundNbt.of(stack));
    }

    public PartialItem getItem() {
        return this.item;
    }

    public PartialCompoundNbt getNbt() {
        return this.nbt;
    }

    public void setItem(PartialItem item) {
        this.item = item;
    }

    public void setNbt(PartialCompoundNbt nbt) {
        this.nbt = nbt;
    }

    @Override
    public boolean isSubsetOf(PartialStack other) {
        if(!this.item.isSubsetOf(other.item)) return false;
        if(!this.nbt.isSubsetOf(other.nbt)) return false;
        return true;
    }

    @Override
    public boolean isSubsetOf(ItemStack stack) {
        if(!this.item.isSubsetOf(stack)) return false;
        if(!this.nbt.isSubsetOf(stack)) return false;
        return true;
    }

    @Override
    public void fillInto(PartialStack other) {
        this.item.fillInto(other.item);
        this.nbt.fillInto(other.nbt);
    }

    @Override
    public Optional<ItemStack> generate(int count) {
        return this.item.generate(count).map(stack -> {
            ItemUtils.setNbt(stack, this.nbt.asWhole().orElse(null));
            return stack;
        });
    }

    @Override
    public boolean test(PartialItem item, PartialCompoundNbt nbt) {
        return this.isSubsetOf(PartialStack.of(item, nbt));
    }

    @Override
    public void validate(String path) {
        this.item.validate(path);
        this.nbt.validate(path);
    }

    @Override
    public PartialStack copy() {
        return new PartialStack(this.item.copy(), this.nbt.copy());
    }

    @Override
    public String toString() {
        return this.item.toString() + this.nbt.toString();
    }

    public static class Adapter implements ISimpleAdapter<PartialStack, NbtElement, JsonElement> {
        @Override
        public void writeBits(PartialStack value, BitBuffer buffer) {
            buffer.writeBoolean(value != null);

            if(value != null) {
                Adapters.PARTIAL_ITEM.writeBits(value.item, buffer);
                Adapters.PARTIAL_NBT.writeBits(value.nbt, buffer);
            }
        }

        @Override
        public Optional<PartialStack> readBits(BitBuffer buffer) {
            if(buffer.readBoolean()) {
                return Optional.of(new PartialStack(
                        Adapters.PARTIAL_ITEM.readBits(buffer).orElse(null),
                        Adapters.PARTIAL_NBT.readBits(buffer).orElse(null)
                ));
            }

            return Optional.empty();
        }

        @Override
        public Optional<NbtElement> writeNbt(PartialStack value) {
            if(value == null) {
                return Optional.empty();
            }

            NbtCompound nbt = new NbtCompound();
            Adapters.PARTIAL_ITEM.writeNbt(value.item).ifPresent(tag -> nbt.put("item", tag));
            Adapters.PARTIAL_NBT.writeNbt(value.nbt).ifPresent(tag -> nbt.put("nbt", tag));
            return Optional.of(nbt);
        }

        @Override
        public Optional<PartialStack> readNbt(NbtElement nbt) {
            if(nbt instanceof NbtCompound compound) {
                PartialItem item = Adapters.PARTIAL_ITEM.readNbt(compound.get("item")).orElseThrow();
                PartialCompoundNbt tag = Adapters.PARTIAL_NBT.readNbt(compound.get("nbt")).orElseGet(PartialCompoundNbt::empty);
                return Optional.of(PartialStack.of(item, tag));
            }

            return Optional.empty();
        }

        @Override
        public Optional<JsonElement> writeJson(PartialStack value) {
            if(value == null) {
                return Optional.empty();
            }

            JsonObject json = new JsonObject();
            Adapters.PARTIAL_ITEM.writeJson(value.item).ifPresent(tag -> json.add("item", tag));
            Adapters.PARTIAL_NBT.writeJson(value.nbt).ifPresent(tag -> json.add("nbt", tag));
            return Optional.of(json);
        }

        @Override
        public Optional<PartialStack> readJson(JsonElement json) {
            if(json instanceof JsonObject object) {
                PartialItem item = Adapters.PARTIAL_ITEM.readJson(object.get("item")).orElseThrow();
                PartialCompoundNbt tag = Adapters.PARTIAL_NBT.readJson(object.get("nbt")).orElseGet(PartialCompoundNbt::empty);
                return Optional.of(PartialStack.of(item, tag));
            }

            return Optional.empty();
        }
    }

    public static Optional<PartialStack> parse(String string, boolean logErrors) {
        try {
            return Optional.of(parse(new StringReader(string)));
        } catch(CommandSyntaxException | IllegalArgumentException e) {
            if(logErrors) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    public static PartialStack parse(String string) throws CommandSyntaxException {
        return parse(new StringReader(string));
    }

    public static PartialStack parse(StringReader reader) throws CommandSyntaxException {
        return PartialStack.of(PartialItem.parse(reader), PartialCompoundNbt.parse(reader));
    }

}
