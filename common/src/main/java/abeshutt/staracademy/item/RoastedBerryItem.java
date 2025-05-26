package abeshutt.staracademy.item;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;

public class RoastedBerryItem extends Item {

    public RoastedBerryItem(int nutrition, float saturationModifier, boolean snack, boolean alwaysEdible, FoodComponent.StatusEffectEntry... effects) {
        super(new Item.Settings().food(construct(nutrition, saturationModifier, snack, alwaysEdible, effects)));
    }

    private static FoodComponent construct(int nutrition, float saturationModifier, boolean snack, boolean alwaysEdible, FoodComponent.StatusEffectEntry[] effects) {
        FoodComponent.Builder food = new FoodComponent.Builder();
        food.nutrition(nutrition);
        food.saturationModifier(saturationModifier);
        if(snack) food.snack();
        if(alwaysEdible) food.alwaysEdible();

        for(FoodComponent.StatusEffectEntry effect : effects) {
            food.statusEffect(effect.effect(), effect.probability());
        }

        return food.build();
    }

}
