package net.server.channel.packet;

import net.server.MaplePacket;

public record TransferNameResultPacket(String name) implements MaplePacket {
}
