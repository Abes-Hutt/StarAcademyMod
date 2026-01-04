package abeshutt.staracademy.live.api.dto;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CosmeticSlot {
    HEAD("head"),
    TOP("top"),
    BOTTOM("bottom"),
    FEET("feet"),
    BACK("back"),
    FACE("face"),
    NECK("neck"),
    ACCESSORIES("accessories");

    private static final Map<String, CosmeticSlot> ID_TO_VALUE = Arrays.stream(CosmeticSlot.values())
            .collect(Collectors.toUnmodifiableMap(CosmeticSlot::getId, Function.identity()));

    private final String id;

    CosmeticSlot(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public static Optional<CosmeticSlot> fromId(String id) {
        return Optional.ofNullable(ID_TO_VALUE.get(id));
    }
}
