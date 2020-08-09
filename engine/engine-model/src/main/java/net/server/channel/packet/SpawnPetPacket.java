package net.server.channel.packet;

import net.server.MaplePacket;

public record SpawnPetPacket(Byte slot, Boolean lead) implements MaplePacket {
}
