package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import constants.game.GameConstants;
import constants.skills.Aran;
import net.server.AbstractPacketHandler;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;

public class AranComboHandler extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      final MapleCharacter player = client.getPlayer();
      SkillFactory.executeForSkill(player, Aran.COMBO_ABILITY, (skill, skillLevel) -> {
         if (GameConstants.isAran(player.getJob().getId()) && (skillLevel > 0 || player.getJob().getId() == 2000)) {
            final long currentTime = currentServerTime();
            short combo = player.getCombo();
            if ((currentTime - player.getLastCombo()) > 3000 && combo > 0) {
               combo = 0;
            }
            combo++;
            switch (combo) {
               case 10:
               case 20:
               case 30:
               case 40:
               case 50:
               case 60:
               case 70:
               case 80:
               case 90:
               case 100:
                  if (player.getJob().getId() != 2000 && (combo / 10) > skillLevel) {
                     break;
                  }
                  skill.getEffect(combo / 10).applyComboBuff(player, combo);
                  break;
            }
            player.setCombo(combo);
            player.setLastCombo(currentTime);
         }
      });
   }
}
