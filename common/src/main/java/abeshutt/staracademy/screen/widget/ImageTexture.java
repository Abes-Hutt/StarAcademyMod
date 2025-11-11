package abeshutt.staracademy.screen.widget;

import net.minecraft.util.Identifier;

public class ImageTexture {

    private final Identifier id;
    private final int width;
    private final int height;

    public ImageTexture(Identifier id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public Identifier getId() {
        return this.id;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

}
