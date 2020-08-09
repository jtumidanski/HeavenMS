package net.server.channel.packet;

import net.server.MaplePacket;

public record MonsterBookCoverPacket(Integer coverId) implements MaplePacket {
}
