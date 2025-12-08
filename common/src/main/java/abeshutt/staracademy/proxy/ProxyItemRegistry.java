package abeshutt.staracademy.proxy;

import net.minecraft.util.Identifier;

import java.util.Set;

public interface ProxyItemRegistry {

    Set<Identifier> getItemRegistry();

    void setItemRegistry(Set<Identifier> items);

    static Set<Identifier> getItemRegistry(Object object) {
        return ((ProxyItemRegistry)object).getItemRegistry();
    }

    static void setItemRegistry(Object object, Set<Identifier> items) {
        ((ProxyItemRegistry)object).setItemRegistry(items);
    }

}
