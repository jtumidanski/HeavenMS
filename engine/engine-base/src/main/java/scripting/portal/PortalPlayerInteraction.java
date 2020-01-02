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