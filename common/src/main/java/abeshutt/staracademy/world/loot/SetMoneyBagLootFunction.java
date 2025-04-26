package abeshutt.staracademy.world.loot;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.init.ModLootFunctionTypes;
import abeshutt.staracademy.world.random.JavaRandom;
import abeshutt.staracademy.world.roll.IntRoll;
//import com.glisco.numismaticoverhaul.currency.CurrencyResolver;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.nbt.NbtLongArray;

import java.util.List;

public class SetMoneyBagLootFunction extends ConditionalLootFunction {

    private final IntRoll bronze;
    private final IntRoll silver;
    private final IntRoll gold;
    private final boolean combine;

    public SetMoneyBagLootFunction(List<LootCondition> conditions, IntRoll bronze, IntRoll silver, IntRoll gold, boolean combine) {
        super(conditions);
        this.bronze = bronze;
        this.silver = silver;
        this.gold = gold;
        this.combine = combine;
    }

    public LootFunctionType getType() {
        return ModLootFunctionTypes.SET_MONEY_BAG.get();
    }

    public ItemStack process(ItemStack stack, LootContext context) {
        JavaRandom random = JavaRandom.ofInternal(context.getRandom().nextLong());

        long[] values = {
            this.bronze == null ? 0 : this.bronze.get(random),
            this.silver == null ? 0 : this.silver.get(random),
            this.gold == null ? 0 : this.gold.get(random)
        };

        /*
        if(this.combine) {
            values = CurrencyResolver.splitValues(CurrencyResolver.combineValues(values));
        }

        stack.getOrCreateNbt().put("Values", new NbtLongArray(values));
        stack.getOrCreateNbt().putBoolean("Combined", this.combine);*/
        return stack;
    }

    /*
    public static class Serializer extends ConditionalLootFunction.Serializer<SetMoneyBagLootFunction> {
        @Override
        public void toJson(JsonObject json, SetMoneyBagLootFunction function, JsonSerializationContext context) {
            super.toJson(json, function, context);
            Adapters.INT_ROLL.writeJson(function.bronze).ifPresent(tag -> json.add("bronze", tag));
            Adapters.INT_ROLL.writeJson(function.silver).ifPresent(tag -> json.add("silver", tag));
            Adapters.INT_ROLL.writeJson(function.gold).ifPresent(tag -> json.add("gold", tag));
            Adapters.BOOLEAN.writeJson(function.combine).ifPresent(tag -> json.add("combine", tag));
        }

        @Override
        public SetMoneyBagLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return new SetMoneyBagLootFunction(conditions,
                Adapters.INT_ROLL.readJson(json.get("bronze")).orElse(null),
                Adapters.INT_ROLL.readJson(json.get("silver")).orElse(null),
                Adapters.INT_ROLL.readJson(json.get("gold")).orElse(null),
                Adapters.BOOLEAN.readJson(json.get("combine")).orElseThrow(() -> new JsonSyntaxException("Could not parse combine"))
            );
        }
    }*/
}

