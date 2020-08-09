package net.server.channel.packet;

import net.server.MaplePacket;

public record CouponCodePacket(String code) implements MaplePacket {
}
