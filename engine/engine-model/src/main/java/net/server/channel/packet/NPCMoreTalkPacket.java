package net.server.channel.packet;

import net.server.MaplePacket;

public record NPCMoreTalkPacket(Byte lastMessageType, Byte action, String returnText,
                                Integer selection) implements MaplePacket {
}
