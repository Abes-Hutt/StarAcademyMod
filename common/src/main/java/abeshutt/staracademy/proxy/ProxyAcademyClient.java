package abeshutt.staracademy.proxy;

import abeshutt.staracademy.live.AcademyClient;

public interface ProxyAcademyClient {

    AcademyClient getClient();

    static AcademyClient get(Object object) {
        return ((ProxyAcademyClient)object).getClient();
    }

}
