package abeshutt.staracademy.live;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.live.api.dto.DisconnectReason;
import abeshutt.staracademy.live.api.dto.agent.MinecraftGameAgent;
import abeshutt.staracademy.live.api.packet.ChallengeMcAuthPacket;
import abeshutt.staracademy.live.api.packet.HelloPacket;
import com.mojang.authlib.exceptions.*;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.session.Session;

import static abeshutt.staracademy.live.api.dto.DisconnectReason.AUTH_FAILED;

public class AuthManager {

    public AuthManager() {

    }

    public void challenge(AcademyClient client, String serverId) {
        Session session = client.getMinecraft().getSession();
        MinecraftSessionService sessionService = client.getMinecraft().getSessionService();

        try {
            sessionService.joinServer(session.getUuidOrNull(), session.getAccessToken(), serverId);
            client.send(new ChallengeMcAuthPacket(serverId));
        } catch(Exception e) {
            String reason = "Failed to log in: %s.".formatted(e.getMessage());

            if(e instanceof AuthenticationUnavailableException) {
                reason = "The authentication servers are currently not reachable. Please try again.";
            } else if(e instanceof InvalidCredentialsException) {
                reason = "Invalid session (Try restarting your game and the launcher).";
            } else if(e instanceof InsufficientPrivilegesException) {
                reason = "Multiplayer is disabled. Please check your Microsoft account settings.";
            } else if(e instanceof ForcedUsernameChangeException || e instanceof UserBannedException) {
                reason = "You are banned from playing online.";
            }

            StarAcademyMod.LOGGER.error(reason, e);
            client.disconnect(new DisconnectReason(AUTH_FAILED.getCode(), reason));
        }
    }

}
