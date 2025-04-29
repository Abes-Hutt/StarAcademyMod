package abeshutt.staracademy.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentMapImpl;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

public class ItemUtils {

    public static NbtCompound getNbt(ItemStack stack) {
        DataResult<NbtElement> data = ComponentMap.CODEC.encodeStart(NbtOps.INSTANCE, stack.getComponents());
        return (NbtCompound)data.result().filter(nbtElement -> nbtElement instanceof NbtCompound).orElse(null);
    }

    public static void setNbt(ItemStack stack, NbtCompound nbt) {
        DataResult<Pair<ComponentMap, NbtElement>> result = nbt == null ? null : ComponentMap.CODEC.decode(NbtOps.INSTANCE, nbt);
        ComponentMap components = result == null ? null : result.result().map(Pair::getFirst).orElse(null);

        if(components == null) {
            components = new ComponentMapImpl(stack.getDefaultComponents());
        }

        stack.applyComponentsFrom(components);
    }

}
