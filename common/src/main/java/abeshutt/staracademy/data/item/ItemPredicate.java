package abeshutt.staracademy.data.item;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.adapter.array.ArrayAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.nbt.PartialCompoundNbt;
import abeshutt.staracademy.data.tile.OrItemPredicate;
import abeshutt.staracademy.util.ItemUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.Optional;

@FunctionalInterface
public interface ItemPredicate {

    ItemPredicate FALSE = (item, nbt) -> false;
    ItemPredicate TRUE = (item, nbt) -> true;

    boolean test(PartialItem item, PartialCompoundNbt nbt);

    default void validate(String path) {

    }

    default boolean test(PartialStack stack) {
        return this.test(stack.getItem(), stack.getNbt());
    }

    default boolean test(ItemStack stack) {
        return this.test(PartialItem.of(stack.getItem()), PartialCompoundNbt.of(ItemUtils.getNbt(stack)));
    }

    static Optional<ItemPredicate> of(String string, boolean logErrors) {
        if(string.isEmpty()) {
            return Optional.of(TRUE);
        }

        return (switch(string.charAt(0)) {
            case '#' -> PartialItemTag.parse(string, logErrors);
            case '@' -> PartialItemGroup.parse(string, logErrors);
            default -> PartialStack.parse(string, logErrors);
        }).map(o -> (ItemPredicate)o);
    }

    class Adapter implements ISimpleAdapter<ItemPredicate, NbtElement, JsonElement> {
        private static ArrayAdapter<ItemPredicate> LIST = Adapters.ofArray(ItemPredicate[]::new, new Adapter());

        @Override
        public void writeBits(ItemPredicate value, BitBuffer buffer) {
            buffer.writeBoolean(value == null);

            if(value != null) {
                if(value instanceof OrItemPredicate or) {
                    buffer.writeBoolean(true);
                    LIST.writeBits(or.getChildren(), buffer);
                } else {
                    buffer.writeBoolean(false);
                    Adapters.UTF_8.writeBits(value.toString(), buffer);
                }
            }
        }

        @Override
        public final Optional<ItemPredicate> readBits(BitBuffer buffer) {
            if(buffer.readBoolean()) {
                return Optional.empty();
            }

            if(buffer.readBoolean()) {
                return LIST.readBits(buffer).map(OrItemPredicate::new);
            }

            return Adapters.UTF_8.readBits(buffer).map(string -> of(string, true).orElse(FALSE));
        }

        @Override
        public Optional<NbtElement> writeNbt(ItemPredicate value) {
            if(value == null) {
                return Optional.empty();
            } else if(value instanceof OrItemPredicate or) {
                return LIST.writeNbt(or.getChildren());
            }

            return Optional.of(NbtString.of(value.toString()));
        }

        @Override
        public Optional<ItemPredicate> readNbt(NbtElement nbt) {
            if(nbt == null) {
                return Optional.empty();
            }

            if(nbt instanceof NbtList list) {
                return LIST.readNbt(list).map(OrItemPredicate::new);
            } else if(nbt instanceof NbtString string) {
                return Optional.of(of(string.asString(), true).orElse(FALSE));
            }

            return Optional.empty();
        }

        @Override
        public Optional<JsonElement> writeJson(ItemPredicate value) {
            if(value == null) {
                return Optional.empty();
            } else if(value instanceof OrItemPredicate or) {
                return LIST.writeJson(or.getChildren());
            }

            return Optional.of(new JsonPrimitive(value.toString()));
        }

        @Override
        public Optional<ItemPredicate> readJson(JsonElement json) {
            if(json == null) {
                return Optional.empty();
            }

            if(json instanceof JsonArray array) {
                return LIST.readJson(array).map(OrItemPredicate::new);
            } else if(json instanceof JsonPrimitive primitive && primitive.isString()) {
                return Optional.of(of(json.getAsString(), true).orElse(FALSE));
            }

            return Optional.empty();
        }
    }

}
