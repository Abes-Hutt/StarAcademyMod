package abeshutt.staracademy;

import net.minecraft.util.StringIdentifiable;

import java.util.Optional;

public enum CardRarity implements StringIdentifiable {
    COMMON("common"),
    UNCOMMON("uncommon"),
    RARE("rare"),
    EPIC("epic"),
    LEGENDARY("legendary"),
    SHINY("shiny");

    private final String name;

    CardRarity(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public static Optional<CardRarity> fromName(String name) {
        for(CardRarity rarity : CardRarity.values()) {
            if(rarity.name.equals(name)) {
                return Optional.of(rarity);
            }
        }

        return Optional.empty();
    }
}
