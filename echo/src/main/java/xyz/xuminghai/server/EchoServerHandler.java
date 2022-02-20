package xyz.xuminghai.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 2022/2/20 11:46 星期日<br/>
 * 回声服务器处理器
 * @author xuMingHai
 */
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /*
        使用@Sharable表示可以被多个Channel共享
        继承入站适配器
     */

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 读取客户端发送的消息
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("服务端收到：" + byteBuf.toString(CharsetUtil.UTF_8));
        // 将接收到的消息发送给发送者，而不冲刷出站消息
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // 读取全部数据后，写入空的缓冲并刷新
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                // 当完成时，添加监听关闭该信道
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 打印异常栈跟踪
        cause.printStackTrace();
        // 关闭信道
        ctx.close();
    }
}
