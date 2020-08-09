package net.server.channel.packet;

import net.server.MaplePacket;

public record ChangeMapSpecialPacket(String startWarp) implements MaplePacket {
}
