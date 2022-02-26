package xyz.xuminghai.monitor;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

/**
 * 2022/2/26 17:37 星期六<br/>
 * udp的监视器
 * @author xuMingHai
 */
public class MessageMonitor {

    public static void main(String[] args) throws InterruptedException {
        final NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        final Bootstrap bootstrap = new Bootstrap();

        // 初始化信道
        {
            bootstrap.channel(NioDatagramChannel.class)
                    .group(eventExecutors)
                    // 开启广播
                    .option(ChannelOption.SO_BROADCAST, true)
                    // 设置处理器
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<DatagramPacket>() {

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
                                    System.out.println(msg);
                                    System.out.println(msg.content().toString(CharsetUtil.UTF_8));
                                }
                            });
                        }
                    });
        }
        // 启动监听
        bootstrap.bind(2333).addListener(future -> System.out.println("监听程序已启动。。。")).sync();

    }

}
