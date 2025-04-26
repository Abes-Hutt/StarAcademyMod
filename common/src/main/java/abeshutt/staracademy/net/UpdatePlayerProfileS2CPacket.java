package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.world.data.PlayerProfileData;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.CustomPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UpdatePlayerProfileS2CPacket extends ModPacket<ClientPlayNetworkHandler> {

    public static final Id<UpdatePlayerProfileS2CPacket> ID = new Id<>(StarAcademyMod.id("update_player_profile"));

    private Map<UUID, GameProfile> profiles;

    public UpdatePlayerProfileS2CPacket() {

    }

    public UpdatePlayerProfileS2CPacket(Map<UUID, GameProfile> profiles) {
        this.profiles = profiles;
    }

    public UpdatePlayerProfileS2CPacket(UUID uuid, GameProfile profile) {
        this.profiles = new HashMap<>();
        this.profiles.put(uuid, profile);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void onReceive(ClientPlayNetworkHandler listener) {
        Map<UUID, GameProfile> profiles = PlayerProfileData.CLIENT.getProfiles();

        if(this.profiles == null) {
            profiles.clear();
        } else {
            this.profiles.forEach((uuid, profile) -> {
                if(profile == null) {
                    profiles.remove(uuid);
                } else {
                    profiles.put(uuid, profile);
                }
            });
        }
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.BOOLEAN.writeBits(this.profiles == null, buffer);

        if(this.profiles != null) {
            Adapters.INT_SEGMENTED_3.writeBits(this.profiles.size(), buffer);

            this.profiles.forEach((uuid, profile) -> {
                Adapters.UUID.writeBits(uuid, buffer);
                Adapters.GAME_PROFILE.asNullable().writeBits(profile, buffer);
            });
        }
    }

    @Override
    public void readBits(BitBuffer buffer) {
        if(Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
            this.profiles = null;
        } else {
            this.profiles = new HashMap<>();
            int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

            for(int i = 0; i < size; i++) {
                this.profiles.put(
                    Adapters.UUID.readBits(buffer).orElseThrow(),
                    Adapters.GAME_PROFILE.asNullable().readBits(buffer).orElse(null)
                );
            }
        }
    }

}
