package abeshutt.staracademy.config;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.entity.EntityPredicate;
import abeshutt.staracademy.data.item.ItemPredicate;
import abeshutt.staracademy.data.tile.TilePredicate;
import abeshutt.staracademy.world.roll.IntRoll;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
            .registerTypeHierarchyAdapter(IntRoll.class, Adapters.INT_ROLL)
            .registerTypeAdapter(BlockPos.class, Adapters.BLOCK_POS)
            .registerTypeAdapter(Identifier.class, Adapters.IDENTIFIER)
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
