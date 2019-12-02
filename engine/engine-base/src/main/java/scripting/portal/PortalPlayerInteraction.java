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
package scripting.portal;

import client.MapleClient;
import database.provider.CharacterProvider;
import scripting.AbstractPlayerInteraction;
import scripting.map.MapScriptManager;
import server.maps.MaplePortal;
import database.DatabaseConnection;
import tools.PacketCreator;
import tools.packet.showitemgaininchat.ShowSpecialEffect;

public class PortalPlayerInteraction extends AbstractPlayerInteraction {

   private MaplePortal portal;

   public PortalPlayerInteraction(MapleClient c, MaplePortal portal) {
      super(c);
      this.portal = portal;
   }

   public MaplePortal getPortal() {
      return portal;
   }

   public void runMapScript() {
      MapScriptManager msm = MapScriptManager.getInstance();
      msm.runMapScript(c, "onUserEnter/" + portal.getScriptName(), false);
   }

   public boolean hasLevel30Character() {
      return DatabaseConnection.getInstance().withConnectionResult(connection ->
            CharacterProvider.getInstance().getCharacterLevels(connection, getPlayer().getAccountID()).stream().anyMatch(level -> level >= 30)
      ).orElse(getPlayer().getLevel() >= 30);
   }

   public void blockPortal() {
      c.getPlayer().blockPortal(getPortal().getScriptName());
   }

   public void unblockPortal() {
      c.getPlayer().unblockPortal(getPortal().getScriptName());
   }

   public void playPortalSound() {
      PacketCreator.announce(c, new ShowSpecialEffect(7));
   }
}