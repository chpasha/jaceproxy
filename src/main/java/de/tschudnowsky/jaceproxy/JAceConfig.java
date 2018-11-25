package de.tschudnowsky.jaceproxy;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.util.FileSize;
import lombok.Getter;
import picocli.CommandLine;

/**
 * User: pavel
 * Date: 02.11.18
 * Time: 10:23
 *
 * TODO print supported requests
 */
@Getter
@CommandLine.Command(name = "java -jar jaceproxy-XX-all.jar",
        description = "Run Acestream2Http Proxy Server")
public class JAceConfig {

    public static final JAceConfig INSTANCE = new JAceConfig();

    static class LevelConverter implements CommandLine.ITypeConverter<Level> {

        @Override
        public Level convert(String value) {
            return Level.valueOf(value);
        }
    }

    static class FileSizeConverter implements CommandLine.ITypeConverter<FileSize> {

        @Override
        public FileSize convert(String value) {
            return FileSize.valueOf(value);
        }
    }

    @CommandLine.Option(names = {"--ace-host"}, description = "Acestream host, default 127.0.0.1")
    private String aceHost = "127.0.0.1";

    @CommandLine.Option(names = {"--ace-port"}, description = "Acestream control port, default 62062")
    private Integer acePort = 62062;

    @CommandLine.Option(names = {"--port"}, description = "Proxy port, default 8000")
    private Integer port = 8000;

    @CommandLine.Option(names = {"--timeout"}, description = "Timeout in seconds when there is no inbound traffic, default 30sec.")
    private Integer timeout = 30;

    @CommandLine.Option(names = {"--restart-on-timeout"}, description = "Should broadcast restart on timeout, default true")
    private Boolean restartOnTimeout = true;

    @CommandLine.Option(names = {"--log-level"}, converter = LevelConverter.class,
            description = "Logging verbosity, default is INFO. Allowed values are ERROR, WARN, INFO, DEBUG, TRACE, OFF")
    private Level logLevel = Level.INFO;

    @CommandLine.Option(names = {"--log-file"}, description = "Path to log file, by default logging is done only to console")
    private String logFile = null;

    @CommandLine.Option(names = {"--log-file-max-size"}, converter = FileSizeConverter.class,
            description = "The max file size of the log to be rotated (archived). Default is 10MB. Can be specified in bytes, kilobytes, megabytes or gigabytes by suffixing a numeric value with KB, MB and respectively GB. For example, 5000000, 5000KB, 5MB and 2GB are all valid values, with the first three being equivalent")
    private FileSize logFileMaxSize = FileSize.valueOf("10MB");

    @CommandLine.Option(names = {"--log-file-max-count"},
            description = "Maximum amount of archived log files to keep. Default is 10. Each file will be as big as --log-file-max-size is")
    private int logFileMaxCount = 10;

    @CommandLine.Option(names = {"-h","--help"}, usageHelp = true, description = "display this help message")
    private boolean usageHelpRequested = false;

    @CommandLine.Option(names = {"-v", "--version"}, versionHelp = true, description = "display version info")
    private boolean versionInfoRequested = false;

    private JAceConfig() {
    }

    @Override
    public String toString() {
        return "JAceConfig{" +
                "aceHost='" + aceHost + '\'' + "\n"+
                ", acePort=" + acePort + "\n" +
                ", port=" + port + "\n" +
                ", timeout=" + timeout + "\n" +
                ", restartOnTimeout=" + restartOnTimeout + "\n" +
                ", logLevel=" + logLevel + "\n" +
                ", logFile='" + logFile + '\'' + "\n" +
                ", logFileMaxSize=" + logFileMaxSize + "\n" +
                ", logFileMaxCount=" + logFileMaxCount + "\n" +
                '}';
    }
}
