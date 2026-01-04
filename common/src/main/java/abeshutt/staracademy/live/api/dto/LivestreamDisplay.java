package abeshutt.staracademy.live.api.dto;

public class LivestreamDisplay {

    private final String pageUrl;
    private final String profilePictureUrl;
    private final String displayName;
    private final String displayHint;
    private final int displayStatusColor;

    public LivestreamDisplay(String pageUrl, String profilePictureUrl, String displayName, String displayHint, int displayStatusColor) {
        this.pageUrl = pageUrl;
        this.profilePictureUrl = profilePictureUrl;
        this.displayName = displayName;
        this.displayHint = displayHint;
        this.displayStatusColor = displayStatusColor;
    }

    public String getPageUrl() {
        return this.pageUrl;
    }

    public String getProfilePictureUrl() {
        return this.profilePictureUrl;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getDisplayHint() {
        return this.displayHint;
    }

    public int getDisplayStatusColor() {
        return this.displayStatusColor;
    }

}
