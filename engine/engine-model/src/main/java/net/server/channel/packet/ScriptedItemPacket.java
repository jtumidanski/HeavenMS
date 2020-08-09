package net.server.channel.packet;

import net.server.MaplePacket;

public record ScriptedItemPacket(Integer timestamp, Short itemSlot, Integer itemId) implements MaplePacket {
}
