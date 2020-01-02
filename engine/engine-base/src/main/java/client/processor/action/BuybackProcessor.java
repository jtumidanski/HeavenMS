package client.processor.action;

import client.MapleCharacter;
import client.MapleClient;
import server.maps.MapleMap;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.packet.field.effect.PlaySound;
import tools.packet.foreigneffect.ShowForeignBuybackEffect;
import tools.packet.foreigneffect.ShowTitleEarned;
import tools.packet.showitemgaininchat.ShowBuybackEffect;

public class BuybackProcessor {

   public static void processBuyback(MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      boolean buyback;

      c.lockClient();
      try {
         buyback = !chr.isAlive() && chr.couldBuyback();
      } finally {
         c.unlockClient();
      }

      if (buyback) {
         String jobString;
         switch (chr.getJobStyle()) {
            case WARRIOR:
               jobString = "warrior";
               break;

            case MAGICIAN:
               jobString = "magician";
               break;

            case BOWMAN:
               jobString = "bowman";
               break;

            case THIEF:
               jobString = "thief";
               break;

            case BRAWLER:
            case GUNSLINGER:
               jobString = "pirate";
               break;

            default:
               jobString = "beginner";
         }

         chr.healHpMp();
         chr.broadcastStance(chr.isFacingLeft() ? 5 : 4);

         MapleMap map = chr.getMap();
         MasterBroadcaster.getInstance().sendToAllInMap(map, new PlaySound("Buyback/" + jobString));
         MasterBroadcaster.getInstance().sendToAllInMap(map, new ShowTitleEarned(chr.getName() + " just bought back into the game!"));

         PacketCreator.announce(chr, new ShowBuybackEffect());
         MasterBroadcaster.getInstance().sendToAllInMap(map, new ShowForeignBuybackEffect(chr.getId()), false, chr);
      }
   }
}
