package io.quarkus.eureka.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.stream.Stream;

public class HostNameDiscovery {

    public static String getHostname() {
        final String[] defaultInterfaceNames = {"en0", "eth0", "eth1", "eth2"};
        return Stream.of(defaultInterfaceNames)
                .map(HostNameDiscovery::getNetworkInterface)
                .filter(HostNameDiscovery::isNetworkInterfaceUp)
                .map(HostNameDiscovery::extractHostname)
                .filter(s -> s!=null && !s.contains(":"))
                .findFirst()
                .orElse(getLocalHost());
    }

    private static NetworkInterface getNetworkInterface(String name) {
        try {
            return NetworkInterface.getByName(name);
        } catch (SocketException e) {
            return null;
        }
    }

    private static boolean isNetworkInterfaceUp(NetworkInterface networkInterface) {
        try {
            return networkInterface != null && networkInterface.isUp();
        } catch (SocketException e) {
            return false;
        }
    }

    private static String getLocalHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static String extractHostname(final NetworkInterface networkInterface) {
        Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
        if (inetAddresses.hasMoreElements()) {
            return inetAddresses.nextElement().getHostName();
        }
        return null;
    }
}
