package xyz.xuminghai.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * 2022/2/20 12:38 星期日<br/>
 * 回声客户端处理器
 * @author xuMingHai
 */
@ChannelHandler.Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    /*
        使用@Sharable表示可以被多个Channel共享
        继承简单的入站处理器，只允许显式处理特定类型的消息。
     */

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        // 处理服务器发送的消息
        System.out.println("客户端收到：" + msg.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 当被通知信道活跃时，发送一条信息
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello Netty!", CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 当出现异常时，打印异常跟踪栈信息
        cause.printStackTrace();
        // 关闭信道
        ctx.close();
    }
}
