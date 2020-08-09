package net.server.channel.packet;

import net.server.MaplePacket;

public record MonsterBombPacket(Integer objectId) implements MaplePacket {
}
