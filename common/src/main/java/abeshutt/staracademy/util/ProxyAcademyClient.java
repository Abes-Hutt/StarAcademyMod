package abeshutt.staracademy.util;

import abeshutt.staracademy.client.AcademyClient;

public interface ProxyAcademyClient {

    AcademyClient getClient();

    static AcademyClient get(Object object) {
        return ((ProxyAcademyClient)object).getClient();
    }

}
