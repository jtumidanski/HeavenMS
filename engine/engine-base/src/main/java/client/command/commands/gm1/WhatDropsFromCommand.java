package client.command.commands.gm1;

import java.util.Iterator;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MapleMonsterInformationProvider;
import server.life.MonsterDropEntry;
import tools.MessageBroadcaster;
import tools.Pair;
import tools.ServerNoticeType;

public class WhatDropsFromCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Please do @whatdropsfrom <monster name>");
         return;
      }
      String monsterName = player.getLastCommandMessage();
      StringBuilder output = new StringBuilder();
      int limit = 3;
      Iterator<Pair<Integer, String>> listIterator = MapleMonsterInformationProvider.getMobsIDsFromName(monsterName).iterator();
      for (int i = 0; i < limit; i++) {
         if (listIterator.hasNext()) {
            Pair<Integer, String> data = listIterator.next();
            int mobId = data.getLeft();
            String mobName = data.getRight();
            output.append(mobName).append(" drops the following items:\r\n\r\n");
            for (MonsterDropEntry drop : MapleMonsterInformationProvider.getInstance().retrieveDrop(mobId)) {
                  if (drop.chance() == 0 || drop.itemId() == 0) {
                     continue;
                  }
                  float chance = Math.max(1000000 / drop.chance() / (!MapleMonsterInformationProvider.getInstance().isBoss(mobId) ? player.getDropRate() : player.getBossDropRate()), 1);
                  output.append("- #v").append(drop.itemId()).append("# (1/").append((int) chance).append(")\r\n");
            }
            output.append("\r\n");
         }
      }

      c.getAbstractPlayerInteraction().npcTalk(9010000, output.toString());
   }
}
