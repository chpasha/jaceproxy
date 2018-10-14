package de.tschudnowsky.jaceproxy.api.commands;

import static de.tschudnowsky.jaceproxy.api.Message.PROPERTY_SEPARATOR;

/**
 * User: pavel
 * Date: 14.10.18
 * Time: 10:36
 */
public class LoadAsyncCommandMapper<T extends LoadAsyncCommand> implements CommandMapper<T> {

    @Override
    public CharSequence writeAsString(T command) {
        StringBuilder sb =
                new StringBuilder(command.getName())
                        .append(PROPERTY_SEPARATOR)
                        .append(command.getRequestId())
                        .append(PROPERTY_SEPARATOR)
                        .append(command.getType())
                        .append(PROPERTY_SEPARATOR);

        switch (command.getType()) {
            case TORRENT:
                sb.append(((LoadAsyncTorrentCommand) command).getTorrentUrl());
                break;
            case INFOHASH:
                sb.append(((LoadAsyncInfohashCommand) command).getInfohash());
                break;
            case RAW:
                sb.append(((LoadAsyncRawTransportFileCommand) command).getTransportFileAsBase64());
                break;
            case PID:
                sb.append(((LoadAsyncContentIDCommand) command).getContentId());
                break;
        }

        if (command.getType() != LoadAsyncCommand.Type.PID) {
            sb.append(PROPERTY_SEPARATOR)
              .append(command.getDeveloperAffiliateZone());
        }

        return sb.toString();
    }
}
