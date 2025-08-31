package abeshutt.staracademy.client;

import abeshutt.staracademy.StarAcademyMod;
import com.mojang.authlib.exceptions.*;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.session.Session;

public class AuthManager {

    private Boolean authed;

    public AuthManager() {
        this.authed = null;
    }

    public Boolean getAuthed() {
        return this.authed;
    }

    public void setAuthed(Boolean authed) {
        this.authed = authed;
    }

    public void start(AcademyClient client) {
        Session session = client.getMinecraft().getSession();
        client.send(new HelloPacket(session.getUuidOrNull(), session.getUsername()));
    }

    public void challenge(AcademyClient client, String serverId) {
        Session session = client.getMinecraft().getSession();
        MinecraftSessionService sessionService = client.getMinecraft().getSessionService();

        try {
            sessionService.joinServer(session.getUuidOrNull(), session.getAccessToken(), serverId);
            client.send(new CompleteAuthPacket(true, null));
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
            client.send(new CompleteAuthPacket(false, reason));
            client.disconnect();
        }
    }

    public void complete(AcademyClient client, boolean success) {
        if(success) {
            this.authed = true;
            return;
        }

        StarAcademyMod.LOGGER.error("Failed the challenge, failed to authenticate.");
    }

}
