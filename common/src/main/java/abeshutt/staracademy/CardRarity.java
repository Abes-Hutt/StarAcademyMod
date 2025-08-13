package abeshutt.staracademy;

import net.minecraft.util.StringIdentifiable;

public enum CardRarity implements StringIdentifiable {
    COMMON("common", 0xFFFFFF),
    UNCOMMON("uncommon", 0xFFFFFF),
    RARE("rare", 0xFFFFFF),
    EPIC("epic", 0xFFFFFF),
    LEGENDARY("legendary", 0xFFFFFF),
    SHINY("shiny", 0xFFFFFF);

    private final String id;
    private final int color;

    CardRarity(String id, int color) {
        this.id = id;
        this.color = color;
    }

    public String getId() {
        return this.id;
    }

    public int getColor() {
        return this.color;
    }

    @Override
    public String asString() {
        return this.id;
    }
}
