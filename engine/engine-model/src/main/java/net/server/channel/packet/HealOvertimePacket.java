package net.server.channel.packet;

import net.server.MaplePacket;

public record HealOvertimePacket(short healHP, short healMP) implements MaplePacket {
}
