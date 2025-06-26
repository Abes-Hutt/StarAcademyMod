package abeshutt.staracademy.config;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import com.glisco.numismaticoverhaul.block.ShopOffer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import io.wispforest.endec.SerializationAttributes;
import io.wispforest.endec.SerializationContext;
import io.wispforest.owo.serialization.format.nbt.NbtDeserializer;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;

import java.util.*;

public class ShopConfig extends FileConfig {

    @Expose private Map<String, List<JsonElement>> offers;

    @Override
    public String getPath() {
        return "shop";
    }

    public Optional<List<ShopOffer>> parseOffers(String id, RegistryWrapper.WrapperLookup registries) {
        if(!this.offers.containsKey(id)) {
            return Optional.empty();
        }

        List<ShopOffer> offers = new ArrayList<>();

        for(JsonElement offer : this.offers.get(id)) {
            Adapters.COMPOUND_NBT.readJson(offer).ifPresentOrElse(nbt -> {
                ItemStack.fromNbt(registries, nbt.get("sell")).ifPresent(stack -> offers.add(new ShopOffer(stack,
                        Adapters.LONG.readNbt(nbt.get("price")).orElse(1L))));
            }, () -> {
                StarAcademyMod.LOGGER.error("Failed to parse offer: {}", offer);
            });
        }

        return Optional.of(offers);
    }

    @Override
    protected void reset() {
        this.offers = new LinkedHashMap<>();

        this.offers.put("default", Arrays.asList(
                JsonParser.parseString("{'sell': {'id': 'minecraft:apple', 'count': 5}, 'price': 100}".replace("'", "\""))
        ));
    }

}
