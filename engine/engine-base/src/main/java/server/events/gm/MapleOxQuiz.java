package server.events.gm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import client.MapleCharacter;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.TimerManager;
import server.maps.MapleMap;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.Randomizer;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.ui.ShowOXQuiz;

public final class MapleOxQuiz {
   private static MapleDataProvider stringData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/Etc.wz"));
   private int round;
   private int question;
   private MapleMap map;
   private int expGain = 200;

   public MapleOxQuiz(MapleMap map) {
      this.map = map;
      this.round = Randomizer.nextInt(9);
      this.question = 1;
   }

   private static int getOXAnswer(int imageDirectory, int id) {
      return MapleDataTool.getInt(stringData.getData("OXQuiz.img").getChildByPath("" + imageDirectory + "").getChildByPath("" + id + "").getChildByPath("a"));
   }

   private boolean isCorrectAnswer(MapleCharacter chr, int answer) {
      double x = chr.position().getX();
      double y = chr.position().getY();
      if ((x > -234 && y > -26 && answer == 0) || (x < -234 && y > -26 && answer == 1)) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, I18nMessage.from("EVENT_OX_QUIZ_SUCCESS"));
         return true;
      }
      return false;
   }

   public void sendQuestion() {
      int gm = 0;
      for (MapleCharacter mc : map.getCharacters()) {
         if (mc.gmLevel() > 1) {
            gm++;
         }
      }
      final int number = gm;
      MasterBroadcaster.getInstance().sendToAllInMap(map, new ShowOXQuiz(round, question, true));
      TimerManager.getInstance().schedule(() -> {
         MasterBroadcaster.getInstance().sendToAllInMap(map, new ShowOXQuiz(round, question, true));
         List<MapleCharacter> chars = new ArrayList<>(map.getCharacters());

         for (MapleCharacter chr : chars) {
            if (chr != null) // make sure they aren't null... maybe something can happen in 12 seconds.
            {
               if (!isCorrectAnswer(chr, getOXAnswer(round, question)) && !chr.isGM()) {
                  chr.changeMap(chr.getMap().getReturnMap());
               } else {
                  chr.gainExp(expGain, true, true);
               }
            }
         }
         //do question
         if ((round == 1 && question == 29) || ((round == 2 || round == 3) && question == 17) || ((round == 4 || round == 8) && question == 12) || (round == 5 && question == 26) || (round == 9 && question == 44) || ((round == 6 || round == 7) && question == 16)) {
            question = 100;
         } else {
            question++;
         }
         //send question
         if (map.getCharacters().size() - number <= 2) {
            MessageBroadcaster.getInstance().sendMapServerNotice(map, ServerNoticeType.LIGHT_BLUE, "The event has ended");
            map.getPortal("join00").setPortalStatus(true);
            map.setOx(null);
            map.setOxQuiz(false);
            //prizes here
            return;
         }
         sendQuestion();
      }, 30000); // Time to answer = 30 seconds ( Ox Quiz packet shows a 30 second timer.
   }
}
