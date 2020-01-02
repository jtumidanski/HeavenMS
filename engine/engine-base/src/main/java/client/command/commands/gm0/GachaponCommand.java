package client.command.commands.gm0;

import client.MapleClient;
import client.command.Command;
import server.MapleItemInformationProvider;
import server.gachapon.MapleGachapon;

public class GachaponCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleGachapon.Gachapon gachapon = null;
      String search = c.getPlayer().getLastCommandMessage();
      String gachaponName = "";
      String[] names = {"Henesys", "Ellinia", "Perion", "Kerning City", "Sleepywood", "Mushroom Shrine", "Showa Spa Male", "Showa Spa Female", "New Leaf City", "Nautilus Harbor"};
      int[] ids = {9100100, 9100101, 9100102, 9100103, 9100104, 9100105, 9100106, 9100107, 9100109, 9100117};
      for (int i = 0; i < names.length; i++) {
         if (search.equalsIgnoreCase(names[i])) {
            gachaponName = names[i];
            gachapon = MapleGachapon.Gachapon.getByNpcId(ids[i]);
         }
      }
      if (gachapon == null) {
         c.getPlayer().yellowMessage("Please use @gacha <name> where name corresponds to one of the below:");
         for (String name : names) {
            c.getPlayer().yellowMessage(name);
         }
         return;
      }
      StringBuilder talkStr = new StringBuilder("The #b" + gachaponName + "#k Gachapon contains the following items.\r\n\r\n");
      for (int i = 0; i < 2; i++) {
         for (int id : gachapon.getItems(i)) {
            talkStr.append("-").append(MapleItemInformationProvider.getInstance().getName(id)).append("\r\n");
         }
      }
      talkStr.append("\r\nPlease keep in mind that there are items that are in all gachapon machines and are not listed here.");

      c.getAbstractPlayerInteraction().npcTalk(9010000, talkStr.toString());
   }
}
