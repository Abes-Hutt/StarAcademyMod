package abeshutt.staracademy.config;

import abeshutt.staracademy.data.adapter.Adapters;
import com.glisco.numismaticoverhaul.block.ShopOffer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import io.wispforest.endec.SerializationAttributes;
import io.wispforest.endec.SerializationContext;
import io.wispforest.owo.serialization.format.nbt.NbtDeserializer;

import java.util.*;

public class ShopConfig extends FileConfig {

    @Expose private Map<String, List<JsonElement>> offers;

    @Override
    public String getPath() {
        return "shop";
    }

    public Optional<List<ShopOffer>> parseOffers(String id) {
        if(!this.offers.containsKey(id)) {
            return Optional.empty();
        }

        List<ShopOffer> offers = new ArrayList<>();

        for(JsonElement offer : this.offers.get(id)) {
            SerializationContext ctx = SerializationContext.empty();

            Adapters.COMPOUND_NBT.readJson(offer).ifPresent(nbt -> {
                offers.add(ShopOffer.ENDEC.decodeFully(ctx
                                .withAttributes(SerializationAttributes.HUMAN_READABLE),
                        NbtDeserializer::of, nbt));
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
