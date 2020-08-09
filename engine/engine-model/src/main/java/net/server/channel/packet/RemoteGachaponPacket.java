package net.server.channel.packet;

import net.server.MaplePacket;

public record RemoteGachaponPacket(Integer ticket, Integer gachaponId) implements MaplePacket {
}
