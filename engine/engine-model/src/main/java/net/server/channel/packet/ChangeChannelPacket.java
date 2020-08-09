package net.server.channel.packet;

import net.server.MaplePacket;

public record ChangeChannelPacket(Integer channel) implements MaplePacket {
}
