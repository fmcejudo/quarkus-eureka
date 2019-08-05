package io.quarkus.eureka.config;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class EurekaRegisterConfig {

    private final EurekaConfiguration eurekaConfiguration;

    private EurekaRegisterConfig(EurekaConfiguration eurekaConfiguration) {
        this.eurekaConfiguration = eurekaConfiguration;
    }

    static EurekaRegisterConfig from(final EurekaConfiguration eurekaConfiguration) {
        return new EurekaRegisterConfig(eurekaConfiguration);
    }

    Map<String, Object> getMap() {

        final String hostname = getHostname();

        Map<String, Object> instance;

        {
            instance = new HashMap<String, Object>() {{
                put("hostName", hostname);
                put("app", eurekaConfiguration.name.toUpperCase());
                put("vipAddress", eurekaConfiguration.vipAddress);
                put("secureVipAddress", eurekaConfiguration.vipAddress);
                put("ipAddr", hostname);
                put("status", "UP");
                put("port", new HashMap<String, String>() {{
                    put("$", String.valueOf(eurekaConfiguration.port));
                    put("@enabled", "true");
                }});
                put("securePort", new HashMap<String, String>() {{
                    put("$", String.valueOf(eurekaConfiguration.port));
                    put("@enabled", "false");
                }});
                put("healthCheckUrl", String.format("http://%s:%d/info/health", hostname, eurekaConfiguration.port));
                put("statusPageUrl", String.format("http://%s:%d/info/status", hostname, eurekaConfiguration.port));
                put("homePageUrl", String.format("http://%s:%d", hostname, eurekaConfiguration.port));
                put("dataCenterInfo", new HashMap<String, Object>() {{
                    put("@class", "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo");
                    put("name", "MyOwn");
                }});
            }};
        }

        return Collections.singletonMap("instance", instance);
    }

    private String getHostname() {
        final String[] defaultInterfaceNames = {"en0", "eth0", "eth1", "eth2"};
        return Stream.of(defaultInterfaceNames)
                .map(this::getNetworkInterface)
                .filter(this::isNetworkInterfaceUp)
                .map(this::extractHostname)
                .findFirst()
                .orElse(getLocalHost());
    }

    private NetworkInterface getNetworkInterface(String name) {
        try {
            return NetworkInterface.getByName(name);
        } catch (SocketException e) {
            return null;
        }
    }

    private boolean isNetworkInterfaceUp(NetworkInterface networkInterface) {
        try {
            return networkInterface != null && networkInterface.isUp();
        } catch (SocketException e) {
            return false;
        }
    }

    private String getLocalHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractHostname(final NetworkInterface networkInterface) {
        Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
        if (inetAddresses.hasMoreElements()) {
            return inetAddresses.nextElement().getHostName();
        }
        return null;
    }
}
