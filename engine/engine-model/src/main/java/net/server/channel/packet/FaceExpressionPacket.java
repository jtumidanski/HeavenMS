package net.server.channel.packet;

import net.server.MaplePacket;

public record FaceExpressionPacket(Integer emote) implements MaplePacket {
}
