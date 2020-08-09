package net.server.channel.packet.fredrick;

import net.server.MaplePacket;

public record BaseFrederickPacket(Byte operation) implements MaplePacket {
}
