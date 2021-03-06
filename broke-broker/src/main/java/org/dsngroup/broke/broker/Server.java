/*
 * Copyright (c) 2017-2018 Dependable Network and System Lab, National Taiwan University.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dsngroup.broke.broker;

import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.dsngroup.broke.broker.handler.MqttMessageHandler;
import org.dsngroup.broke.protocol.MqttDecoder;
import org.dsngroup.broke.protocol.MqttEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Server class used as an entry instance.
 * An example creation,
 * <code>
 *     Server srv = new Server(port, ctx).run();
 * </code>
 */
public class Server {

    private final ServerContext serverContext;

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    /**
     * The PipelineInitializer is a customized ChannelInitializer for desired pipeline of handlers.
     * <code>
     *     new PipelineInitializer()
     * </code>
     */
    private class PipelineInitializer extends ChannelInitializer<Channel> {

        /**
         * Implement the channel, for the pipeline of handler.
         * @param channel The Netty {@see Channel}
         */
        @Override
        public void initChannel(Channel channel) throws Exception {
            channel.pipeline().addLast("MqttEncoder", MqttEncoder.INSTANCE);
            channel.pipeline().addLast("MqttDecoder", new MqttDecoder());
            // Inject server context from outside.
            channel.pipeline().addLast("MqttMessageHandler", new MqttMessageHandler(serverContext));
        }
    }

    /**
     * The Server constructor construct a basic information of a Server.
     * @param serverContext the {@see SeverContext} instance for associated information.
     */
    public Server(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    /**
     * Serve the server.
     * @throws Exception connection error
     */
    public void serve() throws Exception {

        logger.info("Server is running at 0.0.0.0:" + serverContext.getBoundPort());

        // Serve the bossGroup and workerGroup in fix nums of threads (explicit)
        EventLoopGroup bossGroup = new NioEventLoopGroup(serverContext.getNumOfBoss());
        EventLoopGroup workerGroup = new NioEventLoopGroup(serverContext.getNumOfWorker());
        try {
            ServerBootstrap boots = new ServerBootstrap();

            boots.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new PipelineInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture after = boots.bind(serverContext.getBoundPort()).sync();
            after.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    // TODO: close the server channel gracefully.
    // TODO: 1. Close all children channels. 2. close server channel.
    public void close() {}

    public static void main(String[] args) throws Exception {
        new Server(new ServerContext()).serve();
    }
}
