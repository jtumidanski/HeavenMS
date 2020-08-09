package net.server.channel.packet;

import net.server.MaplePacket;

public record QuestActionPacket(Byte action, Short questId, Integer itemId, Integer npc, Integer selection, Integer x,
                                Integer y) implements MaplePacket {
}
