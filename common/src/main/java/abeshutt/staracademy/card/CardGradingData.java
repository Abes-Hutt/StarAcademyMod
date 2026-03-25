package abeshutt.staracademy.card;

import net.minecraft.item.ItemStack;

import java.time.Instant;

public class CardGradingData {
    public static CardGradingData CLIENT = null;

    private final Instant completionTime;
    private final ItemStack stack;

    public CardGradingData(Instant completionTime, ItemStack stack) {
        this.completionTime = completionTime;
        this.stack = stack;
    }

    public Instant getCompletionTime() {
        return completionTime;
    }

    public ItemStack getStack() {
        return stack;
    }
}
