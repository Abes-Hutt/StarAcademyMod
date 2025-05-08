package abeshutt.staracademy.attribute.path;

import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.NbtElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class AttributePath implements ISerializable<NbtElement, JsonElement> {

    public static final AttributePath EMPTY = new AttributePath(false);

    private boolean absolute;
    private final List<String> parts;

    public AttributePath(boolean absolute, String... parts) {
        this.absolute = absolute;
        this.parts = new ArrayList<>(Arrays.asList(parts));
    }

    public AttributePath(boolean absolute, List<String> folder) {
        this.absolute = absolute;
        this.parts = folder;
    }

    public boolean isAbsolute() {
        return this.absolute;
    }

    public AttributePath toRelative() {
        return new AttributePath(false, this.parts);
    }

    public boolean isEmpty() {
        return this.parts.isEmpty();
    }

    public void split(BiConsumer<String, AttributePath> action) {
        action.accept(this.parts.getFirst(), new AttributePath(false, this.parts.subList(1, this.parts.size() - 1)));
    }

    @Override
    public Optional<JsonElement> writeJson() {
        StringBuilder builder = new StringBuilder(this.absolute ? "/" : "");

        for(String folder : this.parts) {
            builder.append(folder);
            builder.append("/");
        }

        return Optional.of(new JsonPrimitive(builder.toString()));
    }

    @Override
    public void readJson(JsonElement json) {
        if(json instanceof JsonPrimitive primitive && primitive.isString()) {
            String path = primitive.getAsString();

            if(path.startsWith("/")) {
                path = path.substring(1);
                this.absolute = true;
            } else {
                this.absolute = false;
            }

            this.parts.clear();
            String[] parts = path.split("/");
            this.parts.addAll(Arrays.asList(parts));
        }
    }

}
