package de.tschudnowsky.jaceproxy;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import de.tschudnowsky.jaceproxy.proxy.JAceProxyInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.File;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
public class JAceHttpServer {

    private ChannelFuture channel;
    private final EventLoopGroup masterGroup;
    private final EventLoopGroup slaveGroup;

    private JAceHttpServer() {
        masterGroup = new NioEventLoopGroup();
        slaveGroup = new NioEventLoopGroup();
    }

    private void start() {
        log.info("Listening on port {}", JAceConfig.INSTANCE.getPort());

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        try {
            final ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(masterGroup, slaveGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new JAceProxyInitializer())
                    //.option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            channel = bootstrap.bind(JAceConfig.INSTANCE.getPort()).sync();

        } catch (InterruptedException e) {
            log.error("Interrupted", e);
        }
    }

    private void shutdown() {
        log.warn("Shutting down");
        slaveGroup.shutdownGracefully();
        masterGroup.shutdownGracefully();

        try {
            channel.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.info("", e);
        }
    }

    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(JAceConfig.INSTANCE);
        commandLine.parseArgs(args);
        if (JAceConfig.INSTANCE.isUsageHelpRequested()) {
            commandLine.usage(System.out);
        } else if (commandLine.isVersionHelpRequested()) {
            commandLine.printVersionHelp(System.out);
        } else {
            configureLog();
            log.info("Starting server with configuration {}", JAceConfig.INSTANCE);
            new JAceHttpServer().start();
        }
    }

    private static void configureLog() {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = (Logger) LoggerFactory.getLogger("root");
        logger.setLevel(JAceConfig.INSTANCE.getLogLevel());
        if (isNotBlank(JAceConfig.INSTANCE.getLogFile())) {
            logger.addAppender(addFileAppender(lc));
        }
    }

    private static Appender<ILoggingEvent> addFileAppender(LoggerContext lc) {
        PatternLayoutEncoder ple = new PatternLayoutEncoder();

        ple.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} %5p [%15.15t] %X{FILENAME} %-20.20logger{39} : %m%n");
        ple.setContext(lc);
        ple.start();
        RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();
        File logFile = new File(JAceConfig.INSTANCE.getLogFile());
        fileAppender.setName("FILE");
        fileAppender.setFile(logFile.getAbsolutePath());
        fileAppender.setEncoder(ple);
        fileAppender.setContext(lc);
        FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
        rollingPolicy.setContext(lc);
        rollingPolicy.setMinIndex(1);
        rollingPolicy.setMaxIndex(JAceConfig.INSTANCE.getLogFileMaxCount());
        String filePathWithoutExtension = logFile.getAbsolutePath().substring(0, logFile.getAbsolutePath().lastIndexOf("."));
        String extension = logFile.getName().substring(logFile.getName().lastIndexOf(".") + 1);
        rollingPolicy.setFileNamePattern(String.format("%s.%%i.%s.zip", filePathWithoutExtension, extension));
        rollingPolicy.setParent(fileAppender);
        rollingPolicy.start();
        fileAppender.setRollingPolicy(rollingPolicy);
        SizeBasedTriggeringPolicy<ILoggingEvent> triggeringPolicy = new SizeBasedTriggeringPolicy<>();
        triggeringPolicy.setContext(lc);
        triggeringPolicy.setMaxFileSize(JAceConfig.INSTANCE.getLogFileMaxSize());
        triggeringPolicy.start();
        fileAppender.setTriggeringPolicy(triggeringPolicy);
        fileAppender.start();
        return fileAppender;
    }
}