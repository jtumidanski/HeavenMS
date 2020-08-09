package net.server.channel.packet;

import net.server.MaplePacket;

public record GeneralChatPacket(String message, Integer show) implements MaplePacket {
}
