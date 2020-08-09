package net.server.channel.packet;

import net.server.MaplePacket;

public record AssignAPPacket(byte jobId, int[] types, int[] gains) implements MaplePacket {
}
