/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package server.maps;

import java.awt.Point;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import tools.PacketCreator;
import tools.packet.remove.RemoveSummon;
import tools.packet.spawn.SpawnSummon;

/**
 * @author Jan
 */
public class MapleSummon extends AbstractAnimatedMapleMapObject {
   private MapleCharacter owner;
   private byte skillLevel;
   private int skillId, hp;
   private SummonMovementType movementType;

   public MapleSummon(MapleCharacter owner, int skillId, Point pos, SummonMovementType movementType) {
      this.owner = owner;
      this.skillId = skillId;

      SkillFactory.getSkill(skillId).ifPresent(skill -> {
         this.skillLevel = owner.getSkillLevel(skill);
         if (skillLevel == 0) {
            throw new RuntimeException();
         }

         this.movementType = movementType;
         setPosition(pos);
      });
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      PacketCreator.announce(client, new SpawnSummon(getOwner().getId(), getObjectId(),
            getSkill(), getSkillLevel(), getPosition(), getStance(),
            getMovementType().getValue(), isPuppet(), false));
   }

   @Override
   public void sendDestroyData(MapleClient client) {
      PacketCreator.announce(client, new RemoveSummon(this, true));
   }

   public MapleCharacter getOwner() {
      return owner;
   }

   public int getSkill() {
      return skillId;
   }

   public int getHP() {
      return hp;
   }

   public void addHP(int delta) {
      this.hp += delta;
   }

   public SummonMovementType getMovementType() {
      return movementType;
   }

   public boolean isStationary() {
      return (skillId == 3111002 || skillId == 3211002 || skillId == 5211001 || skillId == 13111004);
   }

   public byte getSkillLevel() {
      return skillLevel;
   }

   @Override
   public MapleMapObjectType getType() {
      return MapleMapObjectType.SUMMON;
   }

   public final boolean isPuppet() {
      switch (skillId) {
         case 3111002:
         case 3211002:
         case 13111004:
            return true;
      }
      return false;
   }
}
