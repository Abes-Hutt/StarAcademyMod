package abeshutt.staracademy.util;

public interface ProxyNotifyItemRewardMessage {

    long getId();

    void setId(long id);

    static long getId(Object object) {
        return ((ProxyNotifyItemRewardMessage)object).getId();
    }

    static void setId(Object object, long id) {
        ((ProxyNotifyItemRewardMessage)object).setId(id);
    }

}
