package com.metamystia.server.network;

import com.metamystia.server.config.ConfigManager;
import com.metamystia.server.console.command.CommandManager;
import com.metamystia.server.core.room.RoomManager;
import com.metamystia.server.network.handlers.DebugInboundHandler;
import com.metamystia.server.network.handlers.DebugOutboundHandler;
import com.metamystia.server.network.handlers.MainPacketHandler;
import com.metamystia.server.network.handlers.OutboundPacketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteOrder;

/**
 * Game server for TCP communication with clients.
 */
@Slf4j
public class GameServer {
    private final int port;

    private GameServer(int port) {
        this.port = port;
    }

    private ChannelFuture f;

    @Getter
    private boolean running = false;

    public void run() throws Exception {
        log.info("Starting game server...");

        EventLoopGroup bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch){
                            ChannelPipeline p = ch.pipeline();

                            p.addLast(new DebugInboundHandler());
                            p.addLast(new DebugOutboundHandler());

                            // MemoryPack uses little endian, don't ask me why
                            p.addLast(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, Integer.MAX_VALUE, 0, 4, 0, 4, true));
                            p.addLast(new LengthFieldPrepender(ByteOrder.LITTLE_ENDIAN, 4, 0, false));

                            // p.addLast(new AuthHandler());
                            p.addLast(new OutboundPacketHandler());
                            p.addLast(new MainPacketHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            f = b.bind(port).sync();
            log.info("Game server started on port {}", port);
            running = true;

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();

            CommandManager.shutdown();
            RoomManager.shutdown();
            log.info("Game server stopped");
        }
    }

    public void stop() {
        running = false;
        f.channel().close();
    }

    private static GameServer INSTANCE;

    public static synchronized GameServer getInstance() {
    	if (INSTANCE == null) {
    		INSTANCE = new GameServer(ConfigManager.getConfig().getPort());
    	}
    	return INSTANCE;
    }
}
