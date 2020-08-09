package net.server.channel.packet;

import net.server.MaplePacket;

public record MonsterCarnivalPacket(Integer tab, Integer num) implements MaplePacket {
}
