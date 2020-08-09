package net.server.channel.packet;

import net.server.MaplePacket;

public record AdminChatPacket(Byte mode, String message, Integer noticeType) implements MaplePacket {
}
