/**
 * Copyright 2021 Socket Broker Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.exceptionfactory.socketbroker;

import com.exceptionfactory.socketbroker.configuration.BrokerConfiguration;
import com.exceptionfactory.socketbroker.protocol.http.HttpConnectSocketBroker;
import com.exceptionfactory.socketbroker.configuration.ProxyType;
import com.exceptionfactory.socketbroker.protocol.socks.Socks5SocketBroker;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;

/**
 * Brokered Socket Factory implementation creates Sockets using provided configuration
 */
public class BrokeredSocketFactory extends SocketFactory {
    private final BrokerConfiguration brokerConfiguration;

    private final SocketFactory socketFactory;

    /**
     * Brokered Socket Factory constructor with configuration and SocketFactory that provides supporting proxy connections
     *
     * @param brokerConfiguration Broker Configuration
     * @param socketFactory Socket Factory to be used for creating socket connections to the configured proxy server
     */
    public BrokeredSocketFactory(final BrokerConfiguration brokerConfiguration, final SocketFactory socketFactory) {
        this.brokerConfiguration = Objects.requireNonNull(brokerConfiguration, "Configuration required");
        this.socketFactory = Objects.requireNonNull(socketFactory, "Socket Factory required");
    }

    /**
     * Create Socket using provided Broker Configuration without attempting connection
     *
     * @return Configured Brokered Socket not connected
     * @throws IOException Thrown on failure to create socket using default SocketFactory
     */
    @Override
    public Socket createSocket() throws IOException {
        final SocketBroker socketBroker;
        final ProxyType proxyType = brokerConfiguration.getProxyType();
        if (ProxyType.SOCKS5 == proxyType) {
            socketBroker = new Socks5SocketBroker();
        } else {
            socketBroker = new HttpConnectSocketBroker();
        }
        return new BrokeredSocket(brokerConfiguration, socketBroker, socketFactory);
    }

    /**
     * Create Socket and connect to specified host and port number
     *
     * @param host Host to be resolved using InetAddress.getByName()
     * @param port Port number
     * @return Connected Socket
     * @throws IOException Thrown on failure to create socket connection
     */
    @Override
    public Socket createSocket(final String host, final int port) throws IOException {
        final InetAddress hostAddress = InetAddress.getByName(host);
        return createSocket(hostAddress, port);
    }

    /**
     * Create Socket and connect to specified address and port number
     *
     * @param address Remote Internet Address
     * @param port Port number
     * @return Connected Socket
     * @throws IOException Thrown on failure to create socket connection
     */
    @Override
    public Socket createSocket(final InetAddress address, final int port) throws IOException {
        final Socket socket = createSocket();
        connect(socket, address, port);
        return socket;
    }

    /**
     * Create Socket and connect to specified host and port number after binding to specified local address and port number
     *
     * @param host Host to be resolved using InetAddress.getByName()
     * @param port Port number
     * @param localAddress Local address used for binding connection
     * @param localPort Local port used for binding connection
     * @return Connected Socket
     * @throws IOException Thrown on failure to create socket connection
     */
    @Override
    public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort) throws IOException {
        final InetAddress hostAddress = InetAddress.getByName(host);
        return createSocket(hostAddress, port, localAddress, localPort);
    }

    /**
     * Create Socket and connect to specified address and port number after binding to specified local address and port number
     *
     * @param address Remote Internet Address
     * @param port Port number
     * @param localAddress Local address used for binding connection
     * @param localPort Local port used for binding connection
     * @return Connected Socket
     * @throws IOException Thrown on failure to create socket connection
     */
    @Override
    public Socket createSocket(final InetAddress address, final int port, final InetAddress localAddress, final int localPort) throws IOException {
        final Socket socket = createSocket();
        final InetSocketAddress binding = new InetSocketAddress(localAddress, localPort);
        socket.bind(binding);
        connect(socket, address, port);
        return socket;
    }

    private void connect(final Socket socket, final InetAddress address, final int port) throws IOException {
        final InetSocketAddress endpoint = new InetSocketAddress(address, port);
        socket.connect(endpoint);
    }
}
