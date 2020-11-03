package server.processor;

import client.MapleCharacter;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.packet.field.effect.EnvironmentChange;
import tools.packet.foreigneffect.ShowGuideHint;
import tools.packet.foreigneffect.ShowHint;
import tools.packet.foreigneffect.ShowTitleEarned;
import tools.packet.showitemgaininchat.ShowInfo;
import tools.packet.showitemgaininchat.ShowIntro;
import tools.packet.stat.EnableActions;
import tools.packet.statusinfo.ShowInfoText;

public class PlayerInteractionProcessor {
   private static final Object lock = new Object();

   private static volatile PlayerInteractionProcessor instance;

   public static PlayerInteractionProcessor getInstance() {
      PlayerInteractionProcessor result = instance;
      if (result == null) {
         synchronized (lock) {
            result = instance;
            if (result == null) {
               result = new PlayerInteractionProcessor();
               instance = result;
            }
         }
      }
      return result;
   }

   public void showInfo(MapleCharacter character, String path) {
      PacketCreator.announce(character, new ShowInfo(path));
      PacketCreator.announce(character, new EnableActions());
   }

   public void playSound(MapleCharacter character, String sound) {
      MasterBroadcaster.getInstance().sendToAllInMap(character.getMap(), new EnvironmentChange(sound, 4));
   }

   public void showHint(MapleCharacter character, String msg, int width, int height) {
      PacketCreator.announce(character, new ShowHint(msg, width, height));
      PacketCreator.announce(character, new EnableActions());
   }

   public void guideHint(MapleCharacter character, int hint) {
      PacketCreator.announce(character, new ShowGuideHint(hint));
   }

   public void earnTitle(MapleCharacter character, String message) {
      PacketCreator.announce(character, new ShowTitleEarned(message));
   }
   public void showInfoText(MapleCharacter character, String message) {
      PacketCreator.announce(character, new ShowInfoText(message));
   }

   public void showIntro(MapleCharacter character, String path) {
      PacketCreator.announce(character, new ShowIntro(path));
   }
}
