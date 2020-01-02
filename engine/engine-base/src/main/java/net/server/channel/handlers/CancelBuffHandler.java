package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import constants.skills.Bishop;
import constants.skills.BowMaster;
import constants.skills.Corsair;
import constants.skills.Evan;
import constants.skills.FirePoisonArchMage;
import constants.skills.IceLighteningArchMagician;
import constants.skills.Marksman;
import constants.skills.WindArcher;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.CancelBuffPacket;
import net.server.channel.packet.reader.CancelBuffReader;
import tools.MasterBroadcaster;
import tools.packet.foreigneffect.CancelSkill;

public final class CancelBuffHandler extends AbstractPacketHandler<CancelBuffPacket> {
   @Override
   public Class<CancelBuffReader> getReaderClass() {
      return CancelBuffReader.class;
   }

   @Override
   public void handlePacket(CancelBuffPacket packet, MapleClient client) {
      MapleCharacter character = client.getPlayer();
      switch (packet.sourceId()) {
         case FirePoisonArchMage.BIG_BANG:
         case IceLighteningArchMagician.BIG_BANG:
         case Bishop.BIG_BANG:
         case BowMaster.HURRICANE:
         case Marksman.PIERCING_ARROW:
         case Corsair.RAPID_FIRE:
         case WindArcher.HURRICANE:
         case Evan.FIRE_BREATH:
         case Evan.ICE_BREATH:
            MasterBroadcaster.getInstance().sendToAllInMap(character.getMap(), new CancelSkill(character.getId(), packet.sourceId()), false, character);
            break;
         default:
            SkillFactory.getSkill(packet.sourceId()).ifPresent(skill -> character.cancelEffect(skill.getEffect(1), false, -1));
            break;
      }
   }
}