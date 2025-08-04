package abeshutt.staracademy.client;

import abeshutt.staracademy.StarAcademyMod;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;

import java.util.Random;

public class AcademyClient {

    private final MinecraftClient minecraft;
    private final WebSocketClient socket;

    private int timeout;
    private final AuthManager auth;
    private final CodexManager codex;
    private final OutfitManager outfits;

    public AcademyClient(MinecraftClient minecraft) {
        this.minecraft = minecraft;

        this.socket = new WebSocketClient("wss://cobblemon.academy/live", message -> {
            try {
                JsonElement json = JsonParser.parseString(message);

                if(json instanceof JsonObject object) {
                    AcademyPackets.decode(object).ifPresent(this::receive);
                } else {
                    StarAcademyMod.LOGGER.error("Packet is not an object {}.", message);
                }
            } catch(Exception e) {
                StarAcademyMod.LOGGER.error("Failed to parse packet {}.", message, e);
                this.disconnect();
            }
        });

        this.auth = new AuthManager();
        this.codex = new CodexManager();
        this.outfits = new OutfitManager();
    }

    public MinecraftClient getMinecraft() {
        return this.minecraft;
    }

    public OutfitManager getOutfits() {
        return this.outfits;
    }

    public void connect() {
        this.disconnect();
        this.socket.connect();
        this.auth.start(this);

        while(this.socket.isConnected()) {
            if(Boolean.TRUE.equals(this.auth.getAuthed())) {
                break;
            } else if(Boolean.FALSE.equals(this.auth.getAuthed())) {
                this.disconnect();
                return;
            }

            try { Thread.sleep(1); }
            catch(InterruptedException ignored) { }
        }

        if(!this.codex.isComplete()) {
            this.codex.check(this);
        }
    }

    public void disconnect() {
        this.auth.setAuthed(null);
        this.outfits.getTracked().clear();
        this.socket.close();
        this.timeout = 20 * 60 * 5 + new Random().nextInt(380);
    }

    public void tick() {
        if(!this.socket.isConnected()) {
            if(this.timeout <= 0) {
                this.connect();
            } else {
                this.timeout--;
            }
        } else {
            this.timeout = 0;
        }

        if(!this.socket.isConnected() || Boolean.FALSE.equals(this.auth.getAuthed())) {
            return;
        }

        this.outfits.tick(this);
    }

    public void awaitCodex() {
        while(this.socket.isConnected()) {
            if(this.codex.isComplete()) {
                break;
            }

            try { Thread.sleep(1); }
            catch(InterruptedException ignored) { }
        }
    }

    public synchronized void send(AcademyPacket packet) {
        AcademyPackets.encode(packet).ifPresent(object -> {
            this.socket.send(object.toString());
        });
    }

    public synchronized void receive(AcademyPacket packet) {
        if(this.auth.getAuthed() == null) {
            if(!(packet instanceof ChallengeAuthPacket) && !(packet instanceof CompleteAuthPacket)) {
                StarAcademyMod.LOGGER.error("Unknown remote authentication protocol.", new Exception());
                this.disconnect();
            }
        }

        if(packet instanceof ChallengeAuthPacket payload) {
            this.auth.challenge(this, payload.getServerId());
        } else if(packet instanceof CompleteAuthPacket payload) {
            this.auth.complete(this, payload.isSuccess());
        } else if(packet instanceof UpdateCodexPacket payload) {
            this.codex.receive(this, payload.getAssets(), payload.getData());
        } else if(packet instanceof UpdateOutfitRegistryPacket payload) {
            this.outfits.getRegistry().putAll(payload.getRegistry());
        } else if(packet instanceof UpdateOutfitEntryPacket payload) {
            this.outfits.getEntries().putAll(payload.getEntries());
        }
    }

}
