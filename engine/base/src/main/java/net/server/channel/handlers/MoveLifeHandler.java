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
package net.server.channel.handlers;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import constants.ServerConstants;
import net.server.PacketReader;
import net.server.channel.packet.movement.MoveLifePacket;
import net.server.channel.packet.reader.MoveLifeReader;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.processor.MobSkillProcessor;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.Randomizer;
import tools.packet.movement.MoveMonster;
import tools.packet.movement.MoveMonsterResponse;

//import server.life.MobAttackInfo;
//import server.life.MobAttackInfoFactory;

/**
 * @author Danny (Leifde)
 * @author ExtremeDevilz
 * @author Ronan (HeavenMS)
 */
public final class MoveLifeHandler extends AbstractMoveHandler<MoveLifePacket> {
   @Override
   public Class<? extends PacketReader<MoveLifePacket>> getReaderClass() {
      return MoveLifeReader.class;
   }

   private boolean inRangeInclusive(Byte pVal, Integer pMin, Integer pMax) {
      return !(pVal < pMin) || (pVal > pMax);
   }

   @Override
   public void handlePacket(MoveLifePacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      MapleMap map = player.getMap();

      MapleMapObject mmo = map.getMapObject(packet.objectId());
      if (mmo == null || mmo.type() != MapleMapObjectType.MONSTER) {
         return;
      }

      MapleMonster monster = (MapleMonster) mmo;
      List<MapleCharacter> banishPlayers = null;

      byte rawActivity = packet.rawActivity();
      short pOption = packet.pOption();

      if (rawActivity >= 0) {
         rawActivity = (byte) (rawActivity & 0xFF >> 1);
      }

      boolean isAttack = inRangeInclusive(rawActivity, 24, 41);
      boolean isSkill = inRangeInclusive(rawActivity, 42, 59);

      MobSkill toUse = null;
      int useSkillId = 0, useSkillLevel = 0;

      MobSkill nextUse = null;
      int nextSkillId = 0, nextSkillLevel = 0;

      boolean nextMovementCouldBeSkill = !(isSkill || (packet.pNibbles() != 0));

      int castPos;
      if (isSkill) {
         useSkillId = packet.skillId();
         useSkillLevel = packet.skillLevel();

         castPos = monster.getSkillPos(useSkillId, useSkillLevel);
         if (castPos != -1) {
            toUse = MobSkillFactory.getMobSkill(useSkillId, useSkillLevel);

            if (monster.canUseSkill(toUse, true)) {
               int animationTime = MapleMonsterInformationProvider.getInstance().getMobSkillAnimationTime(toUse);
               if (animationTime > 0 && toUse.skillId() != 129) {
                  MobSkillProcessor.getInstance().applyDelayedEffect(player, monster, toUse, true, animationTime);
               } else {
                  banishPlayers = new LinkedList<>();
                  MobSkillProcessor.getInstance().applyEffect(player, monster, toUse, true, banishPlayers);
               }
            }
         }
      } else {
         castPos = (rawActivity - 24) / 2;

         int atkStatus = monster.canUseAttack(castPos, isSkill);
         if (atkStatus < 1) {
            rawActivity = -1;
            pOption = 0;
         }
      }

      int mobMp = monster.getMp();
      if (nextMovementCouldBeSkill) {
         int noSkills = monster.getNoSkills();
         if (noSkills > 0) {
            int rndSkill = Randomizer.nextInt(noSkills);

            Pair<Integer, Integer> skillToUse = monster.getSkills().get(rndSkill);
            nextSkillId = skillToUse.getLeft();
            nextSkillLevel = skillToUse.getRight();
            nextUse = MobSkillFactory.getMobSkill(nextSkillId, nextSkillLevel);

            if (!(nextUse != null && monster.canUseSkill(nextUse, false) && nextUse.hp() >= (int) (((float) monster.getHp() / monster.getMaxHp()) * 100) && mobMp >= nextUse.mpCon())) {
               // thanks OishiiKawaiiDesu for noticing mobs trying to cast skills they are not supposed to be able

               nextSkillId = 0;
               nextSkillLevel = 0;
               nextUse = null;
            }
         }
      }

      Point startPos = new Point(packet.startX(), packet.startY() - 2);
      Point serverStartPos = new Point(monster.position());

      processMovementList(packet.movementDataList(), monster);

      Boolean aggro = monster.aggroMoveLifeUpdate(player);
      if (aggro == null) {
         return;
      }

      if (nextUse != null) {
         PacketCreator.announce(client, new MoveMonsterResponse(packet.objectId(), packet.moveId(), mobMp, aggro, nextSkillId, nextSkillLevel));
      } else {
         PacketCreator.announce(client, new MoveMonsterResponse(packet.objectId(), packet.moveId(), mobMp, aggro));
      }

      if (packet.hasMovement()) {
         if (ServerConstants.USE_DEBUG_SHOW_RCVD_MVLIFE) {
            System.out.println((isSkill ? "SKILL " : (isAttack ? "ATTCK " : " ")) + "castPos: " + castPos + " rawAct: " + rawActivity + " opt: " + pOption + " skillID: " + useSkillId + " skillLV: " + useSkillLevel + " " + "allowSkill: " + nextMovementCouldBeSkill + " mobMp: " + mobMp);
         }
         byte[] movePacket = PacketCreator.create(new MoveMonster(packet.objectId(), nextMovementCouldBeSkill, rawActivity, useSkillId, useSkillLevel, pOption, startPos, packet.movementList()));
         MasterBroadcaster.getInstance().sendToAllInMapRange(map, character -> movePacket, player, serverStartPos);
         //updatePosition(res, monster, -2); //does this need to be done after the packet is broadcast?
         map.moveMonster(monster, monster.position());
      }

      if (banishPlayers != null) {
         for (MapleCharacter chr : banishPlayers) {
            chr.changeMapBanish(monster.getBanish().map(), monster.getBanish().portal(), monster.getBanish().msg());
         }
      }
   }
}