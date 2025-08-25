package abeshutt.staracademy.config;

import abeshutt.staracademy.attribute.again.Attribute;
import abeshutt.staracademy.card.*;
import abeshutt.staracademy.config.sound.PlaySoundEvent;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.biome.BiomePredicate;
import abeshutt.staracademy.data.entity.EntityPredicate;
import abeshutt.staracademy.data.item.ItemPredicate;
import abeshutt.staracademy.data.tile.TilePredicate;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.util.ItemUseLogic;
import abeshutt.staracademy.world.roll.IntRoll;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class Config {

    protected static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().setLenient()
            .registerTypeHierarchyAdapter(TilePredicate.class, Adapters.TILE_PREDICATE)
            .registerTypeHierarchyAdapter(EntityPredicate.class, Adapters.ENTITY_PREDICATE)
            .registerTypeHierarchyAdapter(ItemPredicate.class, Adapters.ITEM_PREDICATE)
            .registerTypeHierarchyAdapter(BiomePredicate.class, Adapters.BIOME_PREDICATE)
            .registerTypeHierarchyAdapter(IntRoll.class, Adapters.INT_ROLL)
            .registerTypeHierarchyAdapter(PlaySoundEvent.class, PlaySoundEvent.Adapter.INSTANCE)
            .registerTypeAdapter(BlockPos.class, Adapters.BLOCK_POS)
            .registerTypeAdapter(Identifier.class, Adapters.IDENTIFIER)
            .registerTypeAdapter(ItemStack.class, Adapters.ITEM_STACK)
            .registerTypeAdapter(ItemUseLogic.class, Adapters.of(ItemUseLogic::new, false))
            .registerTypeAdapter(Rational.class, Adapters.RATIONAL)
            .registerTypeAdapter(CardEntry.class, Adapters.CARD_ENTRY)
            .registerTypeAdapter(CardIconEntry.class, Adapters.CARD_ICON_ENTRY)
            .registerTypeAdapter(CardModifierEntry.class, Adapters.CARD_MODIFIER_ENTRY)
            .registerTypeAdapter(BoosterPackEntry.class, Adapters.CARD_BOOSTER_ENTRY)
            .registerTypeAdapter(CardAlbumEntry.class, Adapters.CARD_ALBUM_ENTRY)
            .registerTypeAdapter(CardDisplayEntry.class, Adapters.CARD_DISPLAY_ENTRY)
            .registerTypeAdapter(Attribute.class, Adapters.ATTRIBUTE)
            .create();

    public abstract void write() throws IOException;

    public abstract <C extends Config> C read() throws IOException;

    protected abstract void reset();

    protected final void writeFile(Path path, Object file) throws IOException {
        Files.createDirectories(path.getParent());
        FileWriter writer = new FileWriter(path.toFile());
        GSON.toJson(file, writer);
        writer.flush();
        writer.close();
    }

    protected final <C> C readFile(Path path, Type type) throws FileNotFoundException {
        return GSON.fromJson(new FileReader(path.toFile()), type);
    }

}
