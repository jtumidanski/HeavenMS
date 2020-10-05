package net.server.channel.handlers;

import client.MapleClient;
import constants.skills.Bishop;
import constants.skills.BowMaster;
import constants.skills.Brawler;
import constants.skills.ChiefBandit;
import constants.skills.Corsair;
import constants.skills.DarkKnight;
import constants.skills.Evan;
import constants.skills.FirePoisonArchMage;
import constants.skills.FirePoisonMagician;
import constants.skills.Gunslinger;
import constants.skills.Hero;
import constants.skills.IceLighteningArchMagician;
import constants.skills.Marksman;
import constants.skills.NightWalker;
import constants.skills.Paladin;
import constants.skills.ThunderBreaker;
import constants.skills.WindArcher;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.SkillEffectPacket;
import net.server.channel.packet.reader.SkillEffectReader;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.MasterBroadcaster;
import tools.packet.foreigneffect.ShowSkillEffect;

public final class SkillEffectHandler extends AbstractPacketHandler<SkillEffectPacket> {
   @Override
   public Class<SkillEffectReader> getReaderClass() {
      return SkillEffectReader.class;
   }

   @Override
   public void handlePacket(SkillEffectPacket packet, MapleClient client) {
      switch (packet.skillId()) {
         case FirePoisonMagician.EXPLOSION, FirePoisonArchMage.BIG_BANG, IceLighteningArchMagician.BIG_BANG,
               Bishop.BIG_BANG, BowMaster.HURRICANE, Marksman.PIERCING_ARROW, ChiefBandit.CHAKRA,
               Brawler.CORKSCREW_BLOW, Gunslinger.GRENADE, Corsair.RAPID_FIRE, WindArcher.HURRICANE,
               NightWalker.POISON_BOMB, ThunderBreaker.CORKSCREW_BLOW, Paladin.MONSTER_MAGNET,
               DarkKnight.MONSTER_MAGNET, Hero.MONSTER_MAGNET, Evan.FIRE_BREATH,
               Evan.ICE_BREATH -> MasterBroadcaster.getInstance().sendToAllInMap(client.getPlayer().getMap(), new ShowSkillEffect(client.getPlayer().getId(), packet.skillId(), packet.level(), packet.flags(), packet.speed(), packet.aids()), false, client.getPlayer());
         default -> LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, client.getPlayer() + " entered SkillEffectHandler without being handled using " + packet.skillId() + ".");
      }
   }
}