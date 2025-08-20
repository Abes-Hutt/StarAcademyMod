package abeshutt.staracademy.attribute.path;

import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class AttributePath<U> implements ISerializable<NbtElement, JsonElement> {

    private boolean absolute;
    private final List<String> parts;

    protected AttributePath() {
        this.absolute = false;
        this.parts = new ArrayList<>();
    }

    protected AttributePath(boolean absolute, String... parts) {
        this.absolute = absolute;
        this.parts = new ArrayList<>(Arrays.asList(parts));
    }

    protected AttributePath(boolean absolute, List<String> folder) {
        this.absolute = absolute;
        this.parts = folder;
    }

    public static <T> AttributePath<T> empty() {
        return new AttributePath<>(false);
    }

    public static <T> AttributePath<T> absolute(String... parts) {
        return new AttributePath<>(true, parts);
    }

    public static <T> AttributePath<T> relative(String... parts) {
        return new AttributePath<>(false, parts);
    }

    public boolean isAbsolute() {
        return this.absolute;
    }

    public AttributePath<U> toRelative() {
        return new AttributePath<>(false, this.parts);
    }

    public boolean isEmpty() {
        return this.parts.isEmpty();
    }

    public <T> T split(BiFunction<String, AttributePath<U>, T> action) {
        if(this.parts.size() == 1) {
            return action.apply(this.parts.getFirst(), empty());
        }

        return action.apply(this.parts.getFirst(), new AttributePath<>(false, this.parts.subList(1, this.parts.size())));
    }

    @Override
    public Optional<NbtElement> writeNbt() {
        if(!this.absolute && this.parts.isEmpty()) {
            return Optional.empty();
        }

        StringBuilder builder = new StringBuilder(this.absolute ? "/" : "");

        for(int i = 0; i < this.parts.size(); i++) {
            String folder = this.parts.get(i);
            builder.append(folder);

            if(i != this.parts.size() - 1) {
                builder.append("/");
            }
        }

        return Optional.of(NbtString.of(builder.toString()));
    }

    @Override
    public void readNbt(NbtElement nbt) {
        this.parts.clear();

        if(nbt instanceof NbtString string) {
            String path = string.asString();

            if(path.startsWith("/")) {
                path = path.substring(1);
                this.absolute = true;
            } else {
                this.absolute = false;
            }

            String[] parts = path.split("/");
            this.parts.addAll(Arrays.asList(parts));
        }
    }

    @Override
    public Optional<JsonElement> writeJson() {
        if(!this.absolute && this.parts.isEmpty()) {
            return Optional.empty();
        }

        StringBuilder builder = new StringBuilder(this.absolute ? "/" : "");

        for(int i = 0; i < this.parts.size(); i++) {
            String folder = this.parts.get(i);
            builder.append(folder);

            if(i != this.parts.size() - 1) {
                builder.append("/");
            }
        }

        return Optional.of(new JsonPrimitive(builder.toString()));
    }

    @Override
    public void readJson(JsonElement json) {
        this.parts.clear();

        if(json instanceof JsonPrimitive primitive && primitive.isString()) {
            String path = primitive.getAsString();

            if(path.startsWith("/")) {
                path = path.substring(1);
                this.absolute = true;
            } else {
                this.absolute = false;
            }

            String[] parts = path.split("/");
            this.parts.addAll(Arrays.asList(parts));
        }
    }

}
