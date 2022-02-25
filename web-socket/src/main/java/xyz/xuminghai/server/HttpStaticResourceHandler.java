package xyz.xuminghai.server;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 2022/2/23 22:04 星期三<br/>
 * http静态资源处理器（处理HTML、CSS、JS文件）
 * @author xuMingHai
 */
@ChannelHandler.Sharable
public class HttpStaticResourceHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final String WEBSOCKET_FLAG = "/chat";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        if (WEBSOCKET_FLAG.equals(msg.uri())) {
            ctx.fireChannelRead(msg.retain());
        } else {
            final URL resource = ClassLoader.getSystemResource(msg.uri().substring(1));
            // 判断资源是否存在
            Path path;
            if (resource != null &&
                    // 如果资源可以读取，再判断是否不是目录
                    (Files.isReadable(path = Paths.get(resource.toURI())) && !Files.isDirectory(path))) {
                // 创建只读的随机访问文件
                final RandomAccessFile randomAccessFile = new RandomAccessFile(resource.getPath(), "r");
                // http响应对象
                final DefaultHttpResponse response = new DefaultHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
                // 设置响应头信息
                final HttpHeaders headers = response.headers();
                // 设置响应体长度
                headers.set(HttpHeaderNames.CONTENT_LENGTH, randomAccessFile.length());
                switch (msg.uri().split("\\.")[1]) {
                    case "html": headers.set(HttpHeaderNames.CONTENT_TYPE, "text/html; Charset=UTF-8"); break;
                    case "css": headers.set(HttpHeaderNames.CONTENT_TYPE, "text/css; Charset=UTF-8"); break;
                    case "js": headers.set(HttpHeaderNames.CONTENT_TYPE, "text/javascript; Charset=UTF-8"); break;
                    default: headers.set(HttpHeaderNames.CONTENT_TYPE, "text/plain; Charset=UTF-8");
                }
                // 写入http响应
                ctx.write(response);
                ctx.writeAndFlush(new DefaultFileRegion(randomAccessFile.getChannel(), 0, randomAccessFile.length()))
                        // 完成文件传输后关闭连接
                        .addListener(ChannelFutureListener.CLOSE);

            } else {
                // 如果资源不存在返回404
                ctx.writeAndFlush(new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.NOT_FOUND));
            }
        }

    }

}
