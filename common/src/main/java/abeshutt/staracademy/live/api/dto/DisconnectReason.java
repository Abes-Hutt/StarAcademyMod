package abeshutt.staracademy.live.api.dto;

public class DisconnectReason {

    public static final DisconnectReason UNKNOWN = new DisconnectReason(0, null);
    public static final DisconnectReason MALFORMED_PACKET = new DisconnectReason(1, "Malformed packet");
    public static final DisconnectReason ILLEGAL_PACKET = new DisconnectReason(2, "Illegal packet");
    public static final DisconnectReason INVALID_PROTOCOL = new DisconnectReason(3, "Invalid protocol");
    public static final DisconnectReason INVALID_AGENT = new DisconnectReason(4, "Invalid agent");
    public static final DisconnectReason AUTH_FAILED = new DisconnectReason(5, "Auth failed");
    public static final DisconnectReason TWITCH_AUTH_ANONYMOUS = new DisconnectReason(6, "Anonymous twitch auth");
    public static final DisconnectReason TWITCH_AUTH_DEFECTOR = new DisconnectReason(7, "Defector twitch auth");

    private final int code;
    private final String message;

    public DisconnectReason(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean is(DisconnectReason other) {
        return this.code == other.getCode();
    }

}
