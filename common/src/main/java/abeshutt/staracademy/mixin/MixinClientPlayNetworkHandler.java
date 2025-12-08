package abeshutt.staracademy.mixin;

import abeshutt.staracademy.proxy.ProxyItemRegistry;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler implements ProxyItemRegistry {

    @Unique private Set<Identifier> academy$itemRegistry;

    @Override
    public Set<Identifier> getItemRegistry() {
        return this.academy$itemRegistry;
    }

    @Override
    public void setItemRegistry(Set<Identifier> items) {
        this.academy$itemRegistry = items;
    }

}
