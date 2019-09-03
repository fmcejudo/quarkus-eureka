package io.quarkus.eureka.util;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

public class HostNameDiscovery {

    private static String HOSTNAME;

    public static String getHostname() {
        if (HOSTNAME != null && !HOSTNAME.trim().equals("")) {
            return HOSTNAME;
        }
        HOSTNAME = HostNameDiscovery.getNetworkInterfaces().stream()
                .filter(HostNameDiscovery::hasBroadcast)
                .map(HostNameDiscovery::extractHostname)
                .findFirst()
                .orElseGet(HostNameDiscovery::getLocalHost);
        return HOSTNAME;
    }

    private static List<NetworkInterface> getNetworkInterfaces() {
        try {
            return Collections.list(NetworkInterface.getNetworkInterfaces());
        } catch (SocketException e) {
            return Collections.emptyList();
        }
    }

    private static boolean hasBroadcast(NetworkInterface networkInterface) {
        try {
            if (networkInterface == null
                    || networkInterface.getInterfaceAddresses().stream().allMatch(ia -> ia.getBroadcast() == null)) {
                return false;
            }
            return networkInterface.isUp();
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
        return networkInterface.getInterfaceAddresses().stream()
                .filter(ia -> ia.getBroadcast() != null)
                .findFirst().map(InterfaceAddress::getAddress)
                .map(InetAddress::getHostName)
                .orElseThrow(() -> new RuntimeException("what, there is no broadcast ip"));
    }
}
