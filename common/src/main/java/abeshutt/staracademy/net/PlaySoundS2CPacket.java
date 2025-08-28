package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.util.ServerSound;
import abeshutt.staracademy.data.bit.BitBuffer;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.random.CheckedRandom;

import java.util.Random;

public class PlaySoundS2CPacket extends ModPacket<ClientPlayNetworkHandler> {

    public static final Id<PlaySoundS2CPacket> ID = new Id<>(StarAcademyMod.id("play_sound_s2c"));

    private ServerSound sound;

    public PlaySoundS2CPacket() {

    }

    public PlaySoundS2CPacket(ServerSound sound) {
        this.sound = sound;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void onReceive(ClientPlayNetworkHandler listener) {
        if(Platform.getEnvironment() == Env.CLIENT) {
            this.playSound();
        }
    }

    @Environment(EnvType.CLIENT)
    private void playSound() {
        MinecraftClient.getInstance().getSoundManager().play(this.sound.build(
                new CheckedRandom(new Random().nextLong())
        ));
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.SERVER_SOUND.writeBits(this.sound, buffer);
    }

    @Override
    public void readBits(BitBuffer buffer) {
        this.sound = Adapters.SERVER_SOUND.readBits(buffer).orElseThrow();
    }

}
