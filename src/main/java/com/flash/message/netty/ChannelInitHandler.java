package com.flash.message.netty;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author 作者 hywang 619201932@qq.com
 *
 * @version 创建时间：2019年7月1日 上午9:53:02
 *
 */
public class ChannelInitHandler extends ChannelInitializer<SocketChannel> {

    Map<String, Object> sourceMap = new HashMap<String, Object>();

    /*
     * 远程netty client 实体类rpc调用
     * 
     * @Override protected void initChannel(SocketChannel ch) throws Exception {
     * ChannelPipeline pipeline = ch.pipeline(); //对于二进制数据进行编解码
     * pipeline.addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
     * pipeline.addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
     * pipeline.addLast(new MessageHandler()); }
     */

    public ChannelInitHandler(Map<String, Object> sourceMap) {
        this.sourceMap = sourceMap;
    }

    /**
     * http 调用
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("http-decoder", new HttpRequestDecoder()); // 请求消息解码器
        pipeline.addLast("http-aggregator", new HttpObjectAggregator(65536));// 目的是将多个消息转换为单一的request或者response对象
        pipeline.addLast("http-encoder", new HttpResponseEncoder());// 响应解码器
        pipeline.addLast("http-chunked", new ChunkedWriteHandler());// 目的是支持异步大文件传输（）
        pipeline.addLast("httpServerHandler", new HttpMessageServerHandler(sourceMap));// 业务逻辑
    }

}
