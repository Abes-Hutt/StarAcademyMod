package abeshutt.staracademy.util;

import net.minecraft.nbt.NbtCompound;

public interface ProxyGymData {

    NbtCompound getGymData();

    void setGymData(NbtCompound gymData);

    static NbtCompound getGymData(Object object) {
        return ((ProxyGymData)object).getGymData();
    }

    static void setGymData(Object object, NbtCompound data) {
        ((ProxyGymData)object).setGymData(data);
    }

}
