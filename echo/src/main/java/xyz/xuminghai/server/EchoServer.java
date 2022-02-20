package xyz.xuminghai.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


/**
 * 2022/2/20 12:04 星期日<br/>
 * 回声服务器起始类
 * @author xuMingHai
 */
public class EchoServer {

    public static void main(String[] args) throws InterruptedException {
        // 回声服务器处理器
        final EchoServerHandler echoServerHandler = new EchoServerHandler();
        // 基于Nio的事件驱动组
        final NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        // 服务器的引导器
        final ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {
            // 指定用于处理接收和客户端发送的信息的事件组
            serverBootstrap.group(eventLoopGroup)
                    // 选用服务器的Channel类型
                    .channel(NioServerSocketChannel.class)
                    // 设置服务器地址
                    .localAddress(2333)
                    // 使用通道初始化器，添加子处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 给管道中添加处理器，使用@Sharable注释的处理器，可以共享使用同样的实例
                            ch.pipeline().addLast(echoServerHandler);
                        }
                    });

            // 异步绑定服务器，阻塞完成
            final ChannelFuture channelFuture = serverBootstrap.bind().sync();
            // 绑定完成后，获取信道的关闭事件，阻塞完成
            channelFuture.channel().closeFuture().sync();


        } finally {
            // 最终关闭所有的事件，阻塞完成
            eventLoopGroup.shutdownGracefully().sync();
        }

    }

}
