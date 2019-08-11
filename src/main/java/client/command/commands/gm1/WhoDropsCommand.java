/*
    This file is part of the HeavenMS MapleStory Server, commands OdinMS-based
    Copyleft (L) 2016 - 2018 RonanLana

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

/*
   @Author: Arthur L - Refactored command content into modules
*/
package client.command.commands.gm1;

import java.util.ArrayList;
import java.util.Objects;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import client.database.provider.DropDataProvider;
import server.MapleItemInformationProvider;
import server.life.MapleMonsterInformationProvider;
import tools.DatabaseConnection;
import tools.Pair;

public class WhoDropsCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.dropMessage(5, "Please do @whodrops <item name>");
         return;
      }

      if (c.tryAcquireClient()) {
         try {
            String searchString = player.getLastCommandMessage();
            StringBuilder output = new StringBuilder();

            ArrayList<Pair<Integer, String>> itemData = MapleItemInformationProvider.getInstance().getItemDataByName(searchString);
            if (itemData.size() == 0) {
               player.dropMessage(5, "The item you searched for doesn't exist.");
               return;
            }

            itemData.stream().limit(3).forEach(pair -> {
               output.append("#b").append(pair.getRight()).append("#k is dropped by:\r\n");
               DatabaseConnection.withConnectionResult(connection -> DropDataProvider.getInstance().getMonstersWhoDrop(connection, pair.getLeft()))
                     .ifPresent(monsterIds -> monsterIds.stream()
                           .map(id -> MapleMonsterInformationProvider.getInstance().getMobNameFromId(id))
                           .filter(Objects::nonNull)
                           .forEach(name -> output.append(name).append(", ")));
               output.append("\r\n\r\n");
            });

            c.getAbstractPlayerInteraction().npcTalk(9010000, output.toString());
         } finally {
            c.releaseClient();
         }
      } else {
         player.dropMessage(5, "Please wait a while for your request to be processed.");
      }
   }
}
