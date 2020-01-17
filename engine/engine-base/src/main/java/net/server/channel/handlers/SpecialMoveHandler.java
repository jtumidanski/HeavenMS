package net.server.channel.handlers;

import java.awt.Point;

import client.MapleCharacter;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import config.YamlConfig;
import constants.skills.Brawler;
import constants.skills.Corsair;
import constants.skills.Priest;
import constants.skills.SuperGM;
import net.server.AbstractPacketHandler;
import net.server.PacketReader;
import net.server.Server;
import net.server.channel.packet.reader.SpecialMoveReader;
import net.server.channel.packet.special.BaseSpecialMovePacket;
import net.server.channel.packet.special.MonsterMagnetPacket;
import server.MapleStatEffect;
import server.life.MapleMonster;
import server.processor.StatEffectProcessor;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.GetEnergy;
import tools.packet.character.SkillCoolDown;
import tools.packet.foreigneffect.ShowBuffEffect;
import tools.packet.foreigneffect.ShowBuffEffectWithLevel;
import tools.packet.monster.CatchMonster;
import tools.packet.stat.EnableActions;

public final class SpecialMoveHandler extends AbstractPacketHandler<BaseSpecialMovePacket> {
   @Override
   public Class<? extends PacketReader<BaseSpecialMovePacket>> getReaderClass() {
      return SpecialMoveReader.class;
   }

   @Override
   public void handlePacket(BaseSpecialMovePacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      chr.getAutoBanManager().setTimestamp(4, Server.getInstance().getCurrentTimestamp(), 28);

      Skill skill = SkillFactory.getSkill(packet.skillId()).orElseThrow();
      int skillLevel = chr.getSkillLevel(skill);
      if (packet.skillId() % 10000000 == 1010 || packet.skillId() % 10000000 == 1011) {
         if (chr.getDojoEnergy() < 10000) { // PE hacking or maybe just lagging
            return;
         }
         skillLevel = 1;
         chr.setDojoEnergy(0);
         PacketCreator.announce(client, new GetEnergy("energy", chr.getDojoEnergy()));
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("ENERGY_RESET_DUE_TO_SECRET_SKILL"));
      }
      if (skillLevel == 0 || skillLevel != packet.skillLevel()) {
         return;
      }

      MapleStatEffect effect = skill.getEffect(skillLevel);
      if (effect.getCoolDown() > 0) {
         if (chr.skillIsCooling(packet.skillId())) {
            return;
         } else if (packet.skillId() != Corsair.BATTLE_SHIP) {
            int coolDownTime = effect.getCoolDown();
            if (StatEffectProcessor.getInstance().isHerosWill(packet.skillId()) && YamlConfig.config.server.USE_FAST_REUSE_HERO_WILL) {
               coolDownTime /= 60;
            }

            PacketCreator.announce(client, new SkillCoolDown(packet.skillId(), coolDownTime));
            chr.addCoolDown(packet.skillId(), currentServerTime(), coolDownTime * 1000);
         }
      }
      if (packet instanceof MonsterMagnetPacket) { // Monster Magnet
         int num = ((MonsterMagnetPacket) packet).monsterData().length;
         for (int i = 0; i < num; i++) {
            int mobOid = ((MonsterMagnetPacket) packet).monsterData()[i].monsterId();
            byte success = ((MonsterMagnetPacket) packet).monsterData()[i].success();
            MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new CatchMonster(mobOid, success), false, chr);
            MapleMonster monster = chr.getMap().getMonsterByOid(mobOid);
            if (monster != null) {
               if (!monster.isBoss()) {
                  monster.aggroClearDamages();
                  monster.aggroMonsterDamage(chr, 1);
                  monster.aggroSwitchController(chr, true);
               }
            }
         }
         byte direction = ((MonsterMagnetPacket) packet).direction();
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new ShowBuffEffectWithLevel(chr.getId(), packet.skillId(), chr.getSkillLevel(packet.skillId()), 1, direction), false, chr);
         PacketCreator.announce(client, new EnableActions());
         return;
      } else if (packet.skillId() == Brawler.MP_RECOVERY) {
         SkillFactory.getSkill(packet.skillId()).ifPresent(s -> {
            MapleStatEffect ef = s.getEffect(chr.getSkillLevel(s));
            int lose = chr.safeAddHP(-1 * (chr.getCurrentMaxHp() / ef.getX()));
            int gain = -lose * (ef.getY() / 100);
            chr.addMP(gain);
         });
      } else if (packet.skillId() == SuperGM.HEAL_PLUS_DISPEL) {
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new ShowBuffEffect(chr.getId(), packet.skillId(), chr.getSkillLevel(packet.skillId()), (byte) 3), false, chr);
      }

      Point pos = packet.position();
      if (chr.isAlive()) {
         if (skill.getId() != Priest.MYSTIC_DOOR) {
            if (skill.getId() % 10000000 != 1005) {
               skill.getEffect(skillLevel).applyTo(chr, pos);
            } else {
               skill.getEffect(skillLevel).applyEchoOfHero(chr);
            }
         } else {
            if (client.tryAcquireClient()) {
               try {
                  if (chr.canDoor()) {
                     chr.cancelMagicDoor();
                     skill.getEffect(skillLevel).applyTo(chr, pos);
                  } else {
                     MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("MYSTIC_DOOR_COOL_DOWN"));
                  }
               } finally {
                  client.releaseClient();
               }
            }

            PacketCreator.announce(client, new EnableActions());
         }
      } else {
         PacketCreator.announce(client, new EnableActions());
      }
   }
}