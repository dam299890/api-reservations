package com.microservice.apireservations.connector.configuration;

import java.util.HashMap;

public class HostConfiguration {

    private String host;

    private int port;

    private HashMap<String, EndpointConfiguration> endPoints;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public HashMap<String, EndpointConfiguration> getEndPoints() {
        return endPoints;
    }

    public void setEndPoints(HashMap<String, EndpointConfiguration> endPoints) {
        this.endPoints = endPoints;
    }
}
