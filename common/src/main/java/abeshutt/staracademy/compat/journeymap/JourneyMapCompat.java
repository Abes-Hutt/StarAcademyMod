package abeshutt.staracademy.compat.journeymap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JourneyMapCompat {

    private static final Set<String> MOD_IDS = new   HashSet<>(Arrays.asList(
            "wavimons", "genomons", "cobblemon_reanimodel"
    ));

    public static boolean shouldLogIconData() {
        return false;
    }

    public static boolean isCobblemonLike(String id) {
        return MOD_IDS.contains(id);
    }

}
