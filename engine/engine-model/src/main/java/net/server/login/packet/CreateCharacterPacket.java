package net.server.login.packet;

import net.server.MaplePacket;

public record CreateCharacterPacket(String name, Integer job, Integer face, Integer hair, Integer hairColor,
                                    Integer skinColor, Integer top, Integer bottom, Integer shoes, Integer weapon,
                                    Integer gender) implements MaplePacket {
}
