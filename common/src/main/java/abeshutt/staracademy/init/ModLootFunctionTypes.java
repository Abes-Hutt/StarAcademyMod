package abeshutt.staracademy.init;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.world.loot.SetMoneyBagLootFunction;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.util.Identifier;

public class ModLootFunctionTypes extends ModRegistries {

    public static RegistrySupplier<LootFunctionType<SetMoneyBagLootFunction>> SET_MONEY_BAG;

    public static void register() {
        SET_MONEY_BAG = register(StarAcademyMod.id("set_money_bag"), SetMoneyBagLootFunction.CODEC);
    }

    public static <V extends LootFunction> RegistrySupplier<LootFunctionType<V>> register(Identifier id, MapCodec<V> codec) {
        return register(LOOT_FUNCTION_TYPES, id, () -> new LootFunctionType<>(codec));
    }

}

