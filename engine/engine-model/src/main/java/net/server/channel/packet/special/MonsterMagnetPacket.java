package net.server.channel.packet.special;

import java.awt.Point;

public class MonsterMagnetPacket extends BaseSpecialMovePacket {
   private final MonsterMagnetData[] monsterData;

   private final Byte direction;

   public MonsterMagnetPacket(Integer skillId, Integer skillLevel, Point position, MonsterMagnetData[] monsterData, Byte direction) {
      super(skillId, skillLevel, position);
      this.monsterData = monsterData;
      this.direction = direction;
   }

   public MonsterMagnetData[] monsterData() {
      return monsterData;
   }

   public Byte direction() {
      return direction;
   }
}
