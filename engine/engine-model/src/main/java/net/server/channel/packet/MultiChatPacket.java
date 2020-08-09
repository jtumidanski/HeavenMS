package net.server.channel.packet;

import net.server.MaplePacket;

public record MultiChatPacket(int theType, int recipients, int[] recipientIds, String message) implements MaplePacket {
}
