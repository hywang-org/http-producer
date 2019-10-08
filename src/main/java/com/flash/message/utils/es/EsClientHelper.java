package com.flash.message.utils.es;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class EsClientHelper {

    private static Map<String, TransportClient> clientMap = new ConcurrentHashMap<String, TransportClient>();

    /**
     * 初始化默认的client
     * 
     * @throws UnknownHostException
     */
    @SuppressWarnings("resource")
    public synchronized static TransportClient buildClient(String clusterName,
            String urls) throws UnknownHostException {
        if (!clientMap.containsKey(clusterName)) {
            Settings setting = Settings.builder()
                    .put("cluster.name", clusterName)
                    .put("client.transport.ignore_cluster_name", true).build();
            List<InetSocketTransportAddress> transportAddresses = getAllAddress(
                    urls);
            clientMap.put(clusterName,
                    new PreBuiltTransportClient(setting)
                            .addTransportAddresses(transportAddresses.toArray(
                                    new InetSocketTransportAddress[transportAddresses
                                            .size()])));
        }
        return clientMap.get(clusterName);
    }

    /**
     * 获得所有的地址
     *
     * @return
     * @throws UnknownHostException
     */
    private static List<InetSocketTransportAddress> getAllAddress(String esUrls)
            throws UnknownHostException {
        List<InetSocketTransportAddress> addressList = new ArrayList<InetSocketTransportAddress>();

        String urlStrings[] = esUrls.split(",");
        for (String urlString : urlStrings) {
            String ip;
            int port;
            String arr[] = urlString.trim().split(":");
            ip = arr[0].trim();
            if (arr.length < 2) {
                port = 9300;
            } else {
                port = Integer.parseInt(arr[1].trim());
            }
            addressList.add(new InetSocketTransportAddress(
                    InetAddress.getByName(ip), port));
        }
        return addressList;
    }

}
