package net.server.channel.packet;

import net.server.MaplePacket;

public record MobBanishPlayerPacket(Integer mobId) implements MaplePacket {
}
