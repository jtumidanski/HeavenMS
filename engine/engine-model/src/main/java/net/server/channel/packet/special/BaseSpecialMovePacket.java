package net.server.channel.packet.special;

import java.awt.Point;

import net.server.MaplePacket;

public class BaseSpecialMovePacket implements MaplePacket {
   private final Integer skillId;

   private final Integer skillLevel;

   private final Point position;

   public BaseSpecialMovePacket(Integer skillId, Integer skillLevel, Point position) {
      this.skillId = skillId;
      this.skillLevel = skillLevel;
      this.position = position;
   }

   public Integer skillId() {
      return skillId;
   }

   public Integer skillLevel() {
      return skillLevel;
   }

   public Point position() {
      return position;
   }
}
