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

import javax.net.SocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.util.Objects;

/**
 * Brokered Socket encapsulates Proxy Socket connectivity and provides access to connected Socket properties
 */
public final class BrokeredSocket extends Socket {
    private static final int PORT_DISCONNECTED = 0;

    private static final int TIMEOUT_DISABLED = 0;

    private final Socket proxySocket;

    private final SocketBroker socketBroker;

    private final BrokerConfiguration brokerConfiguration;

    private InetSocketAddress remoteSocketAddress;

    /**
     * Brokered Socket constructor with proxy server address and configured socket broker for handling connection
     *
     * @param brokerConfiguration Broker Configuration
     * @param socketBroker        Socket Broker
     * @param socketFactory       Socket Factory creates Socket used for connecting to Proxy Server
     * @throws IOException Thrown on failure to create Socket using SocketFactory
     */
    public BrokeredSocket(final BrokerConfiguration brokerConfiguration, final SocketBroker socketBroker, final SocketFactory socketFactory) throws IOException {
        this.brokerConfiguration = Objects.requireNonNull(brokerConfiguration, "Broker Configuration required");
        this.socketBroker = Objects.requireNonNull(socketBroker, "Socket Broker required");
        this.proxySocket = Objects.requireNonNull(socketFactory, "Socket Factory required").createSocket();
    }

    /**
     * Bind Proxy Socket to local address
     *
     * @param localAddress Local Address or null to select an available ephemeral port number
     * @throws IOException Thrown on bind failures
     */
    @Override
    public void bind(final SocketAddress localAddress) throws IOException {
        proxySocket.bind(localAddress);
    }

    /**
     * Connect to remote address using configured Proxy Server without timeout specified
     *
     * @param remoteAddress Remote Address of connection requested through Proxy Server
     * @throws BrokeredConnectException Thrown on connection failures
     */
    @Override
    public void connect(final SocketAddress remoteAddress) throws BrokeredConnectException {
        connect(remoteAddress, TIMEOUT_DISABLED);
    }

    /**
     * Connect to remote address using configured Proxy Server with connect timeout specified
     *
     * @param remoteAddress Remote Address of connection requested through Proxy Server
     * @param timeout       Connection timeout to Proxy Server in milliseconds
     * @throws BrokeredConnectException Thrown on connection failures
     */
    @Override
    public void connect(final SocketAddress remoteAddress, final int timeout) throws BrokeredConnectException {
        if (remoteAddress instanceof InetSocketAddress) {
            remoteSocketAddress = (InetSocketAddress) remoteAddress;
            final InetSocketAddress proxySocketAddress = brokerConfiguration.getProxySocketAddress();
            try {
                proxySocket.connect(proxySocketAddress, timeout);
            } catch (final IOException e) {
                final String message = String.format("Proxy Address [%s] connection failed", proxySocketAddress);
                throw new BrokeredConnectException(message, e);
            }
            try {
                socketBroker.connect(proxySocket, remoteSocketAddress, brokerConfiguration);
            } catch (final IOException e) {
                final String message = String.format("Proxy Address [%s] Remote Address [%s] connection failed", proxySocketAddress, remoteAddress);
                throw new BrokeredConnectException(message, e);
            }
        } else {
            throw new IllegalArgumentException(String.format("Remote Address class [%s] not supported", remoteAddress.getClass()));
        }
    }

    /**
     * Get Remote Address of connected endpoint through proxy server
     *
     * @return Remote Address or null when not connected
     */
    @Override
    public InetAddress getInetAddress() {
        return remoteSocketAddress == null ? null : remoteSocketAddress.getAddress();
    }

    /**
     * Get Port of connected Remote Socket Address
     *
     * @return Remote Port of connected Socket Address when connected or 0 when disconnected
     */
    @Override
    public int getPort() {
        return remoteSocketAddress == null ? PORT_DISCONNECTED : remoteSocketAddress.getPort();
    }

    /**
     * Get Remote Socket Address based on connected endpoint through proxy server
     *
     * @return Remote Socket Address or null when not connected
     */
    @Override
    public SocketAddress getRemoteSocketAddress() {
        return remoteSocketAddress;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return proxySocket.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return proxySocket.getOutputStream();
    }

    @Override
    public void close() throws IOException {
        proxySocket.close();
    }

    @Override
    public SocketChannel getChannel() {
        return proxySocket.getChannel();
    }

    @Override
    public boolean getKeepAlive() throws SocketException {
        return proxySocket.getKeepAlive();
    }

    @Override
    public InetAddress getLocalAddress() {
        return proxySocket.getLocalAddress();
    }

    @Override
    public int getLocalPort() {
        return proxySocket.getLocalPort();
    }

    @Override
    public SocketAddress getLocalSocketAddress() {
        return proxySocket.getLocalSocketAddress();
    }

    @Override
    public boolean getOOBInline() throws SocketException {
        return proxySocket.getOOBInline();
    }

    @Override
    public boolean getReuseAddress() throws SocketException {
        return proxySocket.getReuseAddress();
    }

    @Override
    public int getReceiveBufferSize() throws SocketException {
        return proxySocket.getReceiveBufferSize();
    }

    @Override
    public int getSendBufferSize() throws SocketException {
        return proxySocket.getSendBufferSize();
    }

    @Override
    public int getSoLinger() throws SocketException {
        return proxySocket.getSoLinger();
    }

    @Override
    public int getSoTimeout() throws SocketException {
        return proxySocket.getSoTimeout();
    }

    @Override
    public boolean getTcpNoDelay() throws SocketException {
        return proxySocket.getTcpNoDelay();
    }

    @Override
    public int getTrafficClass() throws SocketException {
        return proxySocket.getTrafficClass();
    }

    @Override
    public boolean isBound() {
        return proxySocket.isBound();
    }

    @Override
    public boolean isClosed() {
        return proxySocket.isClosed();
    }

    @Override
    public boolean isConnected() {
        return proxySocket.isConnected();
    }

    @Override
    public boolean isInputShutdown() {
        return proxySocket.isInputShutdown();
    }

    @Override
    public boolean isOutputShutdown() {
        return proxySocket.isOutputShutdown();
    }

    @Override
    public void sendUrgentData(final int data) throws IOException {
        proxySocket.sendUrgentData(data);
    }

    @Override
    public void setKeepAlive(final boolean keepAlive) throws SocketException {
        proxySocket.setKeepAlive(keepAlive);
    }

    @Override
    public void setOOBInline(final boolean oobInline) throws SocketException {
        proxySocket.setOOBInline(oobInline);
    }

    @Override
    public void setPerformancePreferences(final int connectionTime, final int latency, final int bandwidth) {
        proxySocket.setPerformancePreferences(connectionTime, latency, bandwidth);
    }

    @Override
    public void setReceiveBufferSize(final int receiveBufferSize) throws SocketException {
        proxySocket.setReceiveBufferSize(receiveBufferSize);
    }

    @Override
    public void setReuseAddress(final boolean reuseAddress) throws SocketException {
        proxySocket.setReuseAddress(reuseAddress);
    }

    @Override
    public void setSendBufferSize(final int sendBufferSize) throws SocketException {
        proxySocket.setSendBufferSize(sendBufferSize);
    }

    @Override
    public void setSoLinger(final boolean soLinger, final int linger) throws SocketException {
        proxySocket.setSoLinger(soLinger, linger);
    }

    @Override
    public void setSoTimeout(final int timeout) throws SocketException {
        proxySocket.setSoTimeout(timeout);
    }

    @Override
    public void setTcpNoDelay(final boolean tcpNoDelay) throws SocketException {
        proxySocket.setTcpNoDelay(tcpNoDelay);
    }

    @Override
    public void setTrafficClass(final int trafficClass) throws SocketException {
        proxySocket.setTrafficClass(trafficClass);
    }

    @Override
    public void shutdownInput() throws IOException {
        proxySocket.shutdownInput();
    }

    @Override
    public void shutdownOutput() throws IOException {
        proxySocket.shutdownOutput();
    }

    @Override
    public String toString() {
        return String.format("Proxy Address [%s] Remote Address [%s]", brokerConfiguration.getProxySocketAddress(), remoteSocketAddress);
    }
}
