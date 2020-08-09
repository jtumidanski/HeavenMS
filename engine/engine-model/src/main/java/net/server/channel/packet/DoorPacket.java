package net.server.channel.packet;

import net.server.MaplePacket;

public record DoorPacket(Integer ownerId, Boolean backWarp) implements MaplePacket {
}
