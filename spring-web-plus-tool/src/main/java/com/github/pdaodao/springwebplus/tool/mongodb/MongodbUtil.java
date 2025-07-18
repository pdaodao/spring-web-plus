package com.github.pdaodao.springwebplus.tool.mongodb;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.table.DbInfo;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class MongodbUtil {

    private static final Logger LOG = LoggerFactory.getLogger(MongodbUtil.class);

    private static final String HOST_SPLIT_REGEX = ",\\s*";
    private static final Integer DEFAULT_PORT = 27017;
    private static final Map<String, MongoClient> clientMap = new ConcurrentHashMap<>();
    private static Pattern HOST_PORT_PATTERN = Pattern.compile("(?<host>.*):(?<port>\\d+)*");

    public static MongoClient getClient(DbInfo dbInfo) {
        final String authDbName = StrUtil.isBlank(dbInfo.getDbExtName()) ? "admin" : dbInfo.getDbExtName();
        final String key = dbInfo.key() + authDbName;
        if (!clientMap.containsKey(key)) {
            synchronized (MongodbUtil.class) {
                if (!clientMap.containsKey(key)) {
                    final MongodbConfig config = new MongodbConfig();
                    String url = dbInfo.getUrl();
                    // jdbc:mongodb://10.10.77.115:3306
                    if (url.startsWith("jdbc")) {
                        url = url.split("//")[1];
                    }
                    // host:port
                    config.setHostPorts(url);
                    if (StrUtil.isNotBlank(dbInfo.getPassword())) {
                        config.setDatabase(authDbName);
                        config.setUsername(dbInfo.getUsername());
                        config.setPassword(dbInfo.getPassword());
                    }
                    MongoClient client = MongodbUtil.getClient(config);
                    clientMap.put(key, client);
                    return client;
                }
            }
        }
        return clientMap.get(key);
    }

    public static MongoClient getClient(MongodbConfig config) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("连接配置:{}", config);
        }
        if(StrUtil.isNotBlank(config.getUrl())){
            // Replace the placeholder with your Atlas connection string
           // String uri = "mongodb://110.12.102.109:27017";

            // Construct a ServerApi instance using the ServerApi.builder() method
            ServerApi serverApi = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(config.getUrl()))
                    .serverApi(serverApi)
                    .build();

            return MongoClients.create(settings);
        }
        return null;
    }

    public static void close(MongoClient mongoClient, MongoCursor<Document> cursor) {
        if (cursor != null) {
            LOG.info("Start close mongodb cursor");
            cursor.close();
            LOG.info("Close mongodb cursor successfully");
        }

        if (mongoClient != null) {
            LOG.info("Start close mongodb client");
            mongoClient.close();
            LOG.info("Close mongodb client successfully");
        }
    }
}
