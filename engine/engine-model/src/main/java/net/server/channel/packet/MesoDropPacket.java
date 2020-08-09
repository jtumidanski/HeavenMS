package net.server.channel.packet;

import net.server.MaplePacket;

public record MesoDropPacket(Integer meso) implements MaplePacket {
}
