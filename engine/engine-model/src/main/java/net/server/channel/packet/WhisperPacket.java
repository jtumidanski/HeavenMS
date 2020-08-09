package net.server.channel.packet;

import net.server.MaplePacket;

public record WhisperPacket(Byte mode, String recipient, String message) implements MaplePacket {
}
