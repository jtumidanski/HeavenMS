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
import java.awt.Rectangle;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import constants.skills.BlazeWizard;
import constants.skills.Evan;
import constants.skills.FPMage;
import constants.skills.NightWalker;
import constants.skills.Shadower;
import server.MapleStatEffect;
import server.life.MapleMonster;
import server.life.MobSkill;
import tools.PacketCreator;
import tools.packet.remove.RemoveMist;
import tools.packet.spawn.SpawnMist;

/**
 * @author LaiLaiNoob
 */
public class MapleMist extends AbstractMapleMapObject {
   private Rectangle mistPosition;
   private MapleCharacter owner = null;
   private MapleMonster mob = null;
   private MapleStatEffect source;
   private MobSkill skill;
   private boolean isMobMist, isPoisonMist, isRecoveryMist;
   private int skillDelay;

   public MapleMist(Rectangle mistPosition, MapleMonster mob, MobSkill skill) {
      this.mistPosition = mistPosition;
      this.mob = mob;
      this.skill = skill;
      isMobMist = true;
      isPoisonMist = true;
      isRecoveryMist = false;
      skillDelay = 0;
   }

   public MapleMist(Rectangle mistPosition, MapleCharacter owner, MapleStatEffect source) {
      this.mistPosition = mistPosition;
      this.owner = owner;
      this.source = source;
      this.skillDelay = 8;
      this.isMobMist = false;
      this.isRecoveryMist = false;
      this.isPoisonMist = false;
      switch (source.getSourceId()) {
         case Evan.RECOVERY_AURA:
            isRecoveryMist = true;
            break;

         case Shadower.SMOKE_SCREEN: // Smoke Screen
            isPoisonMist = false;
            break;

         case FPMage.POISON_MIST: // FP mist
         case BlazeWizard.FLAME_GEAR: // Flame Gear
         case NightWalker.POISON_BOMB: // Poison Bomb
            isPoisonMist = true;
            break;
      }
   }

   @Override
   public MapleMapObjectType getType() {
      return MapleMapObjectType.MIST;
   }

   @Override
   public Point getPosition() {
      return mistPosition.getLocation();
   }

   @Override
   public void setPosition(Point position) {
      throw new UnsupportedOperationException();
   }

   public Optional<Skill> getSourceSkill() {
      return SkillFactory.getSkill(source.getSourceId());
   }

   public boolean isMobMist() {
      return isMobMist;
   }

   public boolean isPoisonMist() {
      return isPoisonMist;
   }

   public boolean isRecoveryMist() {
      return isRecoveryMist;
   }

   public int getSkillDelay() {
      return skillDelay;
   }

   public MapleMonster getMobOwner() {
      return mob;
   }

   public MapleCharacter getOwner() {
      return owner;
   }

   public Rectangle getBox() {
      return mistPosition;
   }

   public final byte[] makeDestroyData() {
      return PacketCreator.create(new RemoveMist(getObjectId()));
   }

   public final byte[] makeSpawnData() {
      if (owner != null) {
         return SkillFactory.applyForSkill(owner,
               source.getSourceId(),
               (skill, skillLevel) -> spawnMistForOwner(skillLevel),
               new byte[0]);
      }
      return PacketCreator.create(new SpawnMist(getObjectId(), mob.getId(), skill.getSkillId(), skill.getSkillLevel(), this));
   }

   private byte[] spawnMistForOwner(Integer skillLevel) {
      return getSourceSkill().map(skill -> PacketCreator.create(new SpawnMist(getObjectId(), owner.getId(), skill.getId(), skillLevel, this))).orElse(new byte[0]);
   }

   public final byte[] makeFakeSpawnData(int level) {
      if (owner != null) {
         return spawnMistForOwner(level);
      }
      return PacketCreator.create(new SpawnMist(getObjectId(), mob.getId(), skill.getSkillId(), skill.getSkillLevel(), this));
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      client.announce(makeSpawnData());
   }

   @Override
   public void sendDestroyData(MapleClient client) {
      client.announce(makeDestroyData());
   }

   public boolean makeChanceResult() {
      return source.makeChanceResult();
   }
}
