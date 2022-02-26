package xyz.xuminghai.broadcast;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * 2022/2/26 15:54 星期六<br/>
 * 使用udp进行消息广播
 * @author xuMingHai
 */
public class MessageBroadcaster {

    /**
     * 发送广播消息命令的正则
     */
    private static final Pattern SEND_REGEX = Pattern.compile("^send .+");


    public static void main(String[] args) throws InterruptedException {

        final NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        final Bootstrap bootstrap = new Bootstrap();
        // 初始化引导程序
        {
            // 引导设置
            bootstrap.group(eventExecutors)
                    .channel(NioDatagramChannel.class)
                    // 设置 SO_BROADCAST 套接字选项（广播）
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<DatagramPacket>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {

                                }

                            });
                        }
                    });

        }

        try {
            final NioDatagramChannel channel = (NioDatagramChannel) bootstrap.bind(0)
                    .addListener(future -> System.out.println("UPD广播程序启动成功。。。。。。"))
                    .sync().channel();

            final Scanner scanner = new Scanner(System.in);

            final String order = "send [message]\t发送要广播的消息\n" +
                    "quit\t退出程序\n";

            for (;;) {
                System.out.println(order);
                final String nextLine = scanner.nextLine();
                try {
                    if (SEND_REGEX.matcher(nextLine).find()) {
                        // 发送消息，DatagramPacket进行消息传输
                        final ByteBuf byteBuf = channel.alloc().buffer().writeBytes(nextLine.split(" ")[1].getBytes(StandardCharsets.UTF_8));
                        // 使用广播地址
                        channel.writeAndFlush(new DatagramPacket(byteBuf, new InetSocketAddress("255.255.255.255", 2333))).sync();
                    } else if ("quit".equals(nextLine)) {
                        // 关闭广播通道
                        channel.close().addListener(future -> System.out.println("广播程序以关闭。。。")).sync();
                        break;
                    } else {
                        throw new IllegalStateException("输入的命令不合法！！！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } finally {
            // 关闭事件组
            eventExecutors.shutdownGracefully().sync();
        }



    }




}
