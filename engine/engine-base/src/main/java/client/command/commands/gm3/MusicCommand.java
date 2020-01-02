package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import constants.game.GameConstants;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.packet.field.effect.MusicChange;
import tools.packet.npctalk.GetNPCTalk;

public class MusicCommand extends Command {
   {
      setDescription("");
   }

   private static String getSongList() {
      StringBuilder songList = new StringBuilder("Song:\r\n");
      for (String s : GameConstants.GAME_SONGS) {
         songList.append("  ").append(s).append("\r\n");
      }

      return songList.toString();
   }

   @Override
   public void execute(MapleClient c, String[] params) {

      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         String sendMsg = "";

         sendMsg += "Syntax: #r!music <song>#k\r\n\r\n";
         sendMsg += getSongList();

         PacketCreator.announce(c, new GetNPCTalk(1052015, (byte) 0, sendMsg, "00 00", (byte) 0));
         return;
      }

      String song = player.getLastCommandMessage();
      for (String s : GameConstants.GAME_SONGS) {
         if (s.equalsIgnoreCase(song)) {
            MasterBroadcaster.getInstance().sendToAllInMap(player.getMap(), new MusicChange(s));
            player.yellowMessage("Now playing song " + s + ".");
            return;
         }
      }

      String sendMsg = "";
      sendMsg += "Song not found, please enter a song below.\r\n\r\n";
      sendMsg += getSongList();

      PacketCreator.announce(c, new GetNPCTalk(1052015, (byte) 0, sendMsg, "00 00", (byte) 0));
   }
}
