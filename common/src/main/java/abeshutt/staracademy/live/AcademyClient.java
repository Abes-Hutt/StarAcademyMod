package abeshutt.staracademy.live;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.live.api.Protocol;
import abeshutt.staracademy.live.api.adapter.JsonAdapter;
import abeshutt.staracademy.live.api.dto.DisconnectReason;
import abeshutt.staracademy.live.api.dto.Hash;
import abeshutt.staracademy.live.api.dto.agent.MinecraftGameAgent;
import abeshutt.staracademy.live.api.packet.*;
import abeshutt.staracademy.live.api.registry.Registries;
import abeshutt.staracademy.config.APIConfig;
import abeshutt.staracademy.init.ModConfigs;
import com.google.gson.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;

public class AcademyClient {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private final MinecraftClient minecraft;
    private final WebSocketClient socket;

    private int timeout;
    private final AuthManager auth;
    private final CodexManager codex;
    private final CosmeticsManager cosmetics;
    private final LivestreamManager streams;

    public AcademyClient(MinecraftClient minecraft) {
        this.minecraft = minecraft;

        // Hack since this is called so early.
        ModConfigs.API = new APIConfig().read();

        this.socket = new WebSocketClient(ModConfigs.API.getLiveUrl(), message -> {
            try {
                JsonElement json = JsonParser.parseString(message);

                if(json instanceof JsonObject object) {
                    JsonAdapter adapter = new JsonAdapter(Protocol.UNKNOWN);
                    adapter.readTypeObject(Registries.PACKET, object).ifPresent(this::receive);
                } else {
                    StarAcademyMod.LOGGER.error("Packet is not an object {}.", message);
                }
            } catch(Exception e) {
                StarAcademyMod.LOGGER.error("Failed to parse packet {}.", message, e);
                this.disconnect(DisconnectReason.MALFORMED_PACKET);
            }
        });

        this.auth = new AuthManager();
        this.codex = new CodexManager();
        this.cosmetics = new CosmeticsManager(this);
        this.streams = new LivestreamManager();
    }

    public MinecraftClient getMinecraft() {
        return this.minecraft;
    }

    public CosmeticsManager getCosmetics() {
        return this.cosmetics;
    }

    public CodexManager getCodex() {
        return this.codex;
    }

    public LivestreamManager getStreams() {
        return this.streams;
    }

    public void connect() {
        this.disconnect(DisconnectReason.UNKNOWN);
        this.socket.connect();
        Session session = this.getMinecraft().getSession();
        this.send(new HelloPacket(1, new MinecraftGameAgent(StarAcademyMod.VERSION,
                session.getUuidOrNull(), session.getUsername(),
                this.codex.getCodexHash(Hash.Algorithm.MURMUR3_32).orElse(null))));
    }

    public void disconnect(DisconnectReason reason) {
        this.cosmetics.getTracked().clear();

        if (this.socket.isConnected()) {
            this.send(new DisconnectPacket(reason));
        }

        this.socket.close();
        this.timeout = 20 * 10;
    }

    public void tick() {
        if(!this.socket.isConnected()) {
            if(this.timeout <= 0) {
                new Thread(this::connect).start();
            } else {
                this.timeout--;
            }
        } else {
            this.timeout = 0;
        }

        if(!this.socket.isConnected()) {
            return;
        }

        this.cosmetics.tick(this);
        this.streams.tick(this);
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

    public synchronized void send(Packet packet) {
        JsonAdapter adapter = new JsonAdapter(Protocol.UNKNOWN);

        if (adapter.writeTypeObject(Registries.PACKET, packet) instanceof JsonObject json) {
            this.socket.send(GSON.toJson(json));
        }
    }

    public synchronized void receive(Packet packet) {
        if (packet instanceof DisconnectPacket payload) {
            StarAcademyMod.LOGGER.error("Disconnected from live server: [%d] %s."
                    .formatted(payload.getReason().getCode(), payload.getReason().getMessage()));
        } else if(packet instanceof ChallengeMcAuthPacket payload) {
            this.auth.challenge(this, payload.getServerId());
        } else if(packet instanceof UpdateCodexPacket payload) {
            this.codex.receive(this, payload.getZip());
        } else if(packet instanceof UpdateCosmeticsPacket payload) {
            this.cosmetics.receive(payload.isDelta(), payload.getEntries());
            this.codex.setComplete(true);
        } else if(packet instanceof UpdateLivestreamsPacket payload) {
            this.streams.update(payload.getLivestreams());
            this.codex.setComplete(true);
        }
    }

}
