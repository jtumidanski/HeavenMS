package net.server.channel.packet;

import net.server.MaplePacket;

public record RaiseUIStatePacket(Integer questId) implements MaplePacket {
}
