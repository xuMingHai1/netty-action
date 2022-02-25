package xyz.xuminghai.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 2022/2/23 13:25 星期三<br/>
 *
 * @author xuMingHai
 */
public class WebSocketServer {

    public static void main(String[] args) throws InterruptedException {
        // 事件处理组
        final NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        final HttpStaticResourceHandler httpStaticResourceHandler = new HttpStaticResourceHandler();

        final ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {
            serverBootstrap.group(eventExecutors)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(8080)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    // http协议编解码
                                    .addLast(new HttpServerCodec())
                                    // 大文件异步写入
                                    .addLast(new ChunkedWriteHandler())
                                    // 聚合http消息
                                    .addLast(new HttpObjectAggregator(102400))
                                    .addLast(httpStaticResourceHandler)
                                    .addLast(new WebSocketServerProtocolHandler("/chat"))
                                    .addLast(new TextWebSocketFrameHandler());

                        }
                    });
            // 异步绑定服务器，阻塞完成
            final ChannelFuture channelFuture = serverBootstrap.bind().sync();
            // 绑定完成后，获取信道的关闭事件，阻塞完成
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 关闭所有事件执行器
            eventExecutors.shutdownGracefully().sync();
        }


    }

}
