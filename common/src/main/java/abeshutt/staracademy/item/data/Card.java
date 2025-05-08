package abeshutt.staracademy.item.data;

import abeshutt.staracademy.CardRarity;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonObject;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;

public class Card implements ISerializable<NbtCompound, JsonObject> {

    private CardRarity rarity;
    private ModelIdentifier icon;
    private List<CardModifier> modifiers;

    public Card() {
        this.modifiers = new ArrayList<>();
    }

    public List<CardModifier> getModifiers() {
        return this.modifiers;
    }

}
