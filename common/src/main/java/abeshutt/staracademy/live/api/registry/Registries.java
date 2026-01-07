package abeshutt.staracademy.live.api.registry;

import abeshutt.staracademy.live.api.dto.agent.Agent;
import abeshutt.staracademy.live.api.dto.agent.MinecraftGameAgent;
import abeshutt.staracademy.live.api.packet.*;

public class Registries {

    public static final TypeRegistry<Packet> PACKET = new TypeRegistry<Packet>("type")
            .register("hello", HelloPacket.class, HelloPacket::new)
            .register("disconnect", DisconnectPacket.class, DisconnectPacket::new)
            .register("challenge_mc_auth", ChallengeMcAuthPacket.class, ChallengeMcAuthPacket::new)
            .register("update_codex", UpdateCodexPacket.class, UpdateCodexPacket::new)
            .register("update_livestreams", UpdateLivestreamsPacket.class, UpdateLivestreamsPacket::new)
            .register("start_oauth", StartOAuthPacket.class, StartOAuthPacket::new)
            .register("update_cosmetics", UpdateCosmeticsPacket.class, UpdateCosmeticsPacket::new)
            .register("update_player_tracking", UpdatePlayerTrackingPacket.class, UpdatePlayerTrackingPacket::new);

    public static final TypeRegistry<Agent> AGENT = new TypeRegistry<Agent>("kind")
            .register("minecraft_game", MinecraftGameAgent.class, MinecraftGameAgent::new);

}
