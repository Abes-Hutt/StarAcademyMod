package abeshutt.staracademy.world.loot;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.init.ModLootFunctionTypes;
import abeshutt.staracademy.world.random.JavaRandom;
import abeshutt.staracademy.world.roll.IntRoll;
import com.glisco.numismaticoverhaul.NumismaticOverhaul;
import com.glisco.numismaticoverhaul.currency.CurrencyResolver;
import com.glisco.numismaticoverhaul.item.MoneyBagComponent;
import com.glisco.numismaticoverhaul.item.MoneyBagItem;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.nbt.NbtLongArray;

import java.util.Optional;

public class SetMoneyBagLootFunction implements LootFunction {

    public static final MapCodec<SetMoneyBagLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Adapters.INT_ROLL.codecJson().optionalFieldOf("bronze").forGetter(SetMoneyBagLootFunction::getBronze),
            Adapters.INT_ROLL.codecJson().optionalFieldOf("silver").forGetter(SetMoneyBagLootFunction::getSilver),
            Adapters.INT_ROLL.codecJson().optionalFieldOf("gold").forGetter(SetMoneyBagLootFunction::getGold),
            Adapters.BOOLEAN.codecJson().fieldOf("combine").forGetter(SetMoneyBagLootFunction::isCombine)
        ).apply(instance, (bronze, silver, gold, combine) -> new SetMoneyBagLootFunction(
                bronze.orElse(null), silver.orElse(null), gold.orElse(null), combine)));

    private final IntRoll bronze;
    private final IntRoll silver;
    private final IntRoll gold;
    private final boolean combine;

    public SetMoneyBagLootFunction(IntRoll bronze, IntRoll silver, IntRoll gold, boolean combine) {
        this.bronze = bronze;
        this.silver = silver;
        this.gold = gold;
        this.combine = combine;
    }

    public Optional<IntRoll> getBronze() {
        return Optional.ofNullable(this.bronze);
    }

    public Optional<IntRoll> getSilver() {
        return Optional.ofNullable(this.silver);
    }

    public Optional<IntRoll> getGold() {
        return Optional.ofNullable(this.gold);
    }

    public boolean isCombine() {
        return this.combine;
    }

    @Override
    public LootFunctionType<? extends LootFunction> getType() {
        return ModLootFunctionTypes.SET_MONEY_BAG.get();
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        JavaRandom random = JavaRandom.ofInternal(context.getRandom().nextLong());

        long[] values = {
                this.bronze == null ? 0 : this.bronze.get(random),
                this.silver == null ? 0 : this.silver.get(random),
                this.gold == null ? 0 : this.gold.get(random)
        };

        if(this.combine) {
            values = CurrencyResolver.splitValues(CurrencyResolver.combineValues(values));
        }

        stack.set(NumismaticOverhaul.MONEY_BAG_COMPONENT, MoneyBagComponent.of(values));
        return stack;
    }

}

