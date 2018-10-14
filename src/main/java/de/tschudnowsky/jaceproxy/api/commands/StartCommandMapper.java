package de.tschudnowsky.jaceproxy.api.commands;

import org.apache.commons.lang3.StringUtils;

import static de.tschudnowsky.jaceproxy.api.Message.PROPERTY_SEPARATOR;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 10:36
 */
public class StartCommandMapper<T extends StartCommand> implements CommandMapper<T> {

    @Override
    public CharSequence writeAsString(T command) {
        StringBuilder sb =
                new StringBuilder(command.getName())
                        .append(PROPERTY_SEPARATOR)
                        .append(command.getType())
                        .append(PROPERTY_SEPARATOR);

        switch (command.getType()) {
            case TORRENT:
                StartTorrentCommand torrentCommand = (StartTorrentCommand) command;
                sb.append(torrentCommand.getUrl())
                  .append(PROPERTY_SEPARATOR)
                  .append(StringUtils.join(torrentCommand.getFileIndexes(), ","))
                  .append(PROPERTY_SEPARATOR)
                  .append(command.getDeveloperAffiliateZone())
                  .append(PROPERTY_SEPARATOR)
                  .append(torrentCommand.getStreamId());
                break;
            case INFOHASH:
                StartInfohashCommand infohashCommand = (StartInfohashCommand) command;
                sb.append(infohashCommand.getInfohash())
                  .append(PROPERTY_SEPARATOR)
                  .append(StringUtils.join(infohashCommand.getFileIndexes(), ","))
                  .append(PROPERTY_SEPARATOR)
                  .append(command.getDeveloperAffiliateZone());
                break;
            case RAW:
                StartRawCommand rawCommand = (StartRawCommand) command;
                sb.append(rawCommand.getTransportAsBase64())
                  .append(PROPERTY_SEPARATOR)
                  .append(StringUtils.join(rawCommand.getFileIndexes(), ","))
                  .append(PROPERTY_SEPARATOR)
                  .append(command.getDeveloperAffiliateZone());
                break;
            case PID:
                StartPidCommand pidCommand = (StartPidCommand) command;
                sb.append(pidCommand.getContentId())
                  .append(PROPERTY_SEPARATOR)
                  .append(StringUtils.join(pidCommand.getFileIndexes(), ","));
                break;
            case URL:
                StartUrlCommand urlCommand = (StartUrlCommand) command;
                sb.append(urlCommand.getUrl())
                  .append(PROPERTY_SEPARATOR)
                  .append(StringUtils.join(urlCommand.getFileIndexes(), ","))
                  .append(PROPERTY_SEPARATOR)
                  .append(command.getDeveloperAffiliateZone());
                break;
            case EFILE:
                StartFileCommand fileCommand = (StartFileCommand) command;
                sb.append(fileCommand.getFileUrl());
                break;
        }

        sb.append(PROPERTY_SEPARATOR)
          .append("output_format=http");
        return sb.toString();
    }
}
