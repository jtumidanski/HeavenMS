package net.server.channel.packet;

import java.awt.Point;
import java.util.List;
import java.util.Map;

import net.server.MaplePacket;

public record AttackPacket(Integer numAttacked, Integer numDamage, Integer numAttackedAndDamage, Integer skill,
                           Integer skillLevel, Integer stance, Integer direction,
                           Integer rangedDirection, Integer charge, Integer display, Boolean ranged, Boolean magic,
                           Integer speed, Map<Integer, List<Integer>> allDamage,
                           Point position) implements MaplePacket {
}
