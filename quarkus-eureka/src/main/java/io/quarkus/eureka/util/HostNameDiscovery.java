/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        if (HOSTNAME == null || HOSTNAME.trim().equals("")) {
            HOSTNAME = HostNameDiscovery.getNetworkInterfaces().stream()
                    .filter(HostNameDiscovery::hasBroadcast)
                    .map(HostNameDiscovery::extractHostname)
                    .findFirst()
                    .orElseGet(HostNameDiscovery::getLocalHost);
        }
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

        return networkInterface != null
                && networkInterface.getInterfaceAddresses().stream().anyMatch(ia -> ia.getBroadcast() != null);

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
