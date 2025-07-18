package com.github.pdaodao.springwebplus.tool.mongodb;

import com.mongodb.AuthenticationMechanism;

import java.io.Serializable;

public class MongodbConfig implements Serializable {

    private String hostPorts;

    private String url;

    private String username;

    private String password;

    private String authenticationMechanism = AuthenticationMechanism.SCRAM_SHA_1.getMechanismName();

    private String database;

    private String clusterMode;


    private ConnectionConfig mongodbConfig = new ConnectionConfig();

    public String getClusterMode() {
        return clusterMode;
    }

    public void setClusterMode(String clusterMode) {
        this.clusterMode = clusterMode;
    }

    public String getAuthenticationMechanism() {
        return authenticationMechanism;
    }

    public void setAuthenticationMechanism(String authenticationMechanism) {
        this.authenticationMechanism = authenticationMechanism;
    }

    public String getHostPorts() {
        return hostPorts;
    }

    public void setHostPorts(String hostPorts) {
        this.hostPorts = hostPorts;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public ConnectionConfig getMongodbConfig() {
        return mongodbConfig;
    }

    public void setMongodbConfig(ConnectionConfig mongodbConfig) {
        this.mongodbConfig = mongodbConfig;
    }

    @Override
    public String toString() {
        return "MongodbConfig{" +
                "hostPorts='" + hostPorts + '\'' +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='******" + '\'' +
                ", authenticationMechanism='" + authenticationMechanism + '\'' +
                ", database='" + database + '\'' +
                ", clusterMode='" + clusterMode + '\'' +
                ", mongodbConfig=" + mongodbConfig +
                '}';
    }

    public class ConnectionConfig implements Serializable {
        private int connectionsPerHost = 100;

        private int threadsForConnectionMultiplier = 100;

        private int connectionTimeout = 10000;

        private int maxWaitTime = 5000;

        private int socketTimeout = 0;

        public int getConnectionsPerHost() {
            return connectionsPerHost;
        }

        public void setConnectionsPerHost(int connectionsPerHost) {
            this.connectionsPerHost = connectionsPerHost;
        }

        public int getThreadsForConnectionMultiplier() {
            return threadsForConnectionMultiplier;
        }

        public void setThreadsForConnectionMultiplier(int threadsForConnectionMultiplier) {
            this.threadsForConnectionMultiplier = threadsForConnectionMultiplier;
        }

        public int getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        public int getMaxWaitTime() {
            return maxWaitTime;
        }

        public void setMaxWaitTime(int maxWaitTime) {
            this.maxWaitTime = maxWaitTime;
        }

        public int getSocketTimeout() {
            return socketTimeout;
        }

        public void setSocketTimeout(int socketTimeout) {
            this.socketTimeout = socketTimeout;
        }

        @Override
        public String toString() {
            return "ConnectionConfig{" +
                    "connectionsPerHost=" + connectionsPerHost +
                    ", threadsForConnectionMultiplier=" + threadsForConnectionMultiplier +
                    ", connectionTimeout=" + connectionTimeout +
                    ", maxWaitTime=" + maxWaitTime +
                    ", socketTimeout=" + socketTimeout +
                    '}';
        }
    }
}
