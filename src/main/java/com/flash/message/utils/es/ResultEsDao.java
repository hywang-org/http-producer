package com.flash.message.utils.es;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.flash.message.entity.HttpProducerEntity;

@Component
public class ResultEsDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultEsDao.class);

    public static final String TYPE = "Msg_Content";

    private static TransportClient transportClient;

    @Value("${es.result.clusterName}")
    private String clusterName;

    @Value("${es.result.urls}")
    private String urls;

    @Value("${es.result.index}")
    private String index;

    @PostConstruct
    public void init() {
        Assert.hasText(clusterName,"clusterNa must be not null");
        Assert.hasText(urls,"urls must be not null");
        Assert.hasText(index,"index must be not null");
        try {
            transportClient = EsClientHelper.buildClient(clusterName, urls);
        } catch (UnknownHostException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public TransportClient getClient() {
        return transportClient;
    }

    /**
     * Indexes a Result document into an index
     * 
     * @throws Exception
     */
    public IndexResponse indexResult(String documentId, Map<String, Object> jsonMap) {
        return transportClient.prepareIndex(index, TYPE, documentId).setOpType(IndexRequest.OpType.CREATE)
                .setSource(jsonMap).get();
    }

    public GetResponse getResult(String documentId) {
        return transportClient.prepareGet(index, ResultEsDao.TYPE, documentId).get();
    }

    public UpdateResponse updateResultContent(String documentId, String content) throws IOException {
        return transportClient.prepareUpdate(index, ResultEsDao.TYPE, documentId)
                .setDoc(XContentFactory.jsonBuilder().startObject().field("content", content)
                        .field("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                        .endObject())
                .get();
    }

    /**
     * create indexRequest if not able to find data, while update updateRequest
     * if data can be found
     * 
     * @param documentId
     * @param orderId
     * @param audioId
     * @param type
     * @param createTime
     * @param content
     */
    public UpdateResponse upsertRersult(String documentId, String orderId, Long audioId, Integer type, String content) {
        try {
            IndexRequest indexRequest = new IndexRequest(index, ResultEsDao.TYPE, documentId).source(XContentFactory
                    .jsonBuilder().startObject().field("orderId", orderId).field("audioId", audioId).field("type", type)
                    .field("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                    .field("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                    .field("content", content).endObject());

            UpdateRequest updateRequest = new UpdateRequest(index, ResultEsDao.TYPE, documentId).doc(XContentFactory
                    .jsonBuilder().startObject().field("content", content)
                    .field("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).endObject())
                    .upsert(indexRequest);
            return transportClient.update(updateRequest).get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * create indexRequest if not able to find data, while update updateRequest
     * if data can be found
     * 
     * @param common
     * @return
     */
    public UpdateResponse upsertRersult(HttpProducerEntity common) {
    	try {
    		IndexRequest indexRequest = new IndexRequest(index, ResultEsDao.TYPE, common.getOwnMsgId()).source(XContentFactory
                    .jsonBuilder().startObject().field("mobile", common.getMobile()).field("appId", common.getAccount())
                    .field("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                    .field("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                    .field("msgContent", common.getMsg()).endObject());

            UpdateRequest updateRequest = new UpdateRequest(index, ResultEsDao.TYPE, common.getOwnMsgId()).doc(XContentFactory
                    .jsonBuilder().startObject().field("content", common.getMsg())
                    .field("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).endObject())
                    .upsert(indexRequest);
            return transportClient.update(updateRequest).get();
		} catch (IOException | InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
    }

}
