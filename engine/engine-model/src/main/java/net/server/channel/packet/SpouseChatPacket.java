package net.server.channel.packet;

import net.server.MaplePacket;

public record SpouseChatPacket(String recipient, String message) implements MaplePacket {
}
