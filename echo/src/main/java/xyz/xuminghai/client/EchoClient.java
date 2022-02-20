package xyz.xuminghai.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 2022/2/20 12:53 星期日<br/>
 * 回声客户端起始器
 * @author xuMingHai
 */
public class EchoClient {

    public static void main(String[] args) throws InterruptedException {
        // 事件组
        final NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        // 引导类
        final Bootstrap bootstrap = new Bootstrap();

        try {

            // 添加事件组
            bootstrap.group(nioEventLoopGroup)
                    // 选择Channel类型
                    .channel(NioSocketChannel.class)
                    // 设置要连接的地址
                    .remoteAddress("localhost", 2333)
                    // 设置处理器
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 给管道中添加处理器
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });

            // 连接服务器，阻塞完成
            final ChannelFuture channelFuture = bootstrap.connect().sync();
            // 阻塞等待关闭
            channelFuture.channel().closeFuture().sync();


        } finally {
            // 关闭所有事件，阻塞完成
            nioEventLoopGroup.shutdownGracefully().sync();
        }
    }

}
