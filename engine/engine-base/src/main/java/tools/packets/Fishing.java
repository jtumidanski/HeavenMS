package tools.packets;

import java.util.Calendar;

import client.MapleCharacter;
import config.YamlConfig;
import constants.game.GameConstants;
import constants.inventory.ItemConstants;
import server.MapleItemInformationProvider;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.foreigneffect.ShowForeignInfo;
import tools.packet.showitemgaininchat.ShowInfo;

public class Fishing {

   private static double getFishingLikelihood(int x) {
      return 50.0 + 7.0 * (7.0 * Math.sin(x)) * (Math.cos(Math.pow(x, 0.777)));
   }

   public static double[] fetchFishingLikelihood() {
      Calendar calendar = Calendar.getInstance();
      int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

      int hours = calendar.get(Calendar.HOUR);
      int minutes = calendar.get(Calendar.MINUTE);
      int seconds = calendar.get(Calendar.SECOND);

      double yearLikelihood = getFishingLikelihood(dayOfYear);
      double timeLikelihood = getFishingLikelihood(hours + minutes + seconds);

      return new double[]{yearLikelihood, timeLikelihood};
   }

   private static boolean hitFishingTime(MapleCharacter chr, int baitLevel, double yearLikelihood, double timeLikelihood) {
      double baitLikelihood = 0.0002 * chr.getWorldServer().getFishingRate() * baitLevel;   // can improve 10.0 at "max level 50000" on rate 1x

      if (YamlConfig.config.server.USE_DEBUG) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("DEBUG_FISHING_TITLE"));
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("DEBUG_FISHING_BODY").with(yearLikelihood, timeLikelihood, baitLikelihood));
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("DEBUG_FISHING_FOOTER").with((0.23 * yearLikelihood), (0.77 * timeLikelihood), baitLikelihood));
      }

      return (0.23 * yearLikelihood) + (0.77 * timeLikelihood) + (baitLikelihood) > 57.777;
   }

   public static void doFishing(MapleCharacter chr, int baitLevel, double yearLikelihood, double timeLikelihood) {
      if (!chr.isLoggedInWorld() || !chr.isAlive()) {
         return;
      }

      if (!GameConstants.isFishingArea(chr.getMapId())) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, I18nMessage.from("FISHING_ERROR_WRONG_MAP"));
         return;
      }

      if (chr.getLevel() < 30) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("FISHING_ERROR_LEVEL_REQUIREMENT"));
         return;
      }

      String fishingEffect;
      if (!hitFishingTime(chr, baitLevel, yearLikelihood, timeLikelihood)) {
         fishingEffect = "Effect/BasicEff.img/Catch/Fail";
      } else {
         String rewardStr = "";
         fishingEffect = "Effect/BasicEff.img/Catch/Success";

         int rand = (int) (3.0 * Math.random());
         switch (rand) {
            case 0:
               int mesoAward = (int) (1400.0 * Math.random() + 1201) * chr.getMesoRate() + (15 * chr.getLevel() / 5);
               chr.gainMeso(mesoAward, true, true, true);

               rewardStr = mesoAward + " mesos.";
               break;
            case 1:
               int expAward = (int) (645.0 * Math.random() + 620.0) * chr.getExpRate() + (15 * chr.getLevel() / 4);
               chr.gainExp(expAward, true, true);

               rewardStr = expAward + " EXP.";
               break;
            case 2:
               int itemId = getRandomItem();
               rewardStr = "a(n) " + MapleItemInformationProvider.getInstance().getName(itemId) + ".";

               if (chr.canHold(itemId)) {
                  chr.getAbstractPlayerInteraction().gainItem(itemId, true);
               } else {
                  chr.showHint("Couldn't catch a(n) #r" + MapleItemInformationProvider.getInstance().getName(itemId) + "#k due to #e#b" + ItemConstants.getInventoryType(itemId) + "#k#n inventory limit.");
                  rewardStr += ".. but has goofed up due to full inventory.";
               }
               break;
         }

         MessageBroadcaster.getInstance().sendMapServerNotice(chr.getMap(), ServerNoticeType.LIGHT_BLUE, chr.getName() + " found " + rewardStr);
      }

      PacketCreator.announce(chr, new ShowInfo(fishingEffect));
      MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new ShowForeignInfo(chr.getId(), fishingEffect), false, chr);
   }

   public static int getRandomItem() {
      int rand = (int) (100.0 * Math.random());
      int[] commons = {1002851, 2002020, 2002020, 2000006, 2000018, 2002018, 2002024, 2002027, 2002027, 2000018, 2000018, 2000018, 2000018, 2002030, 2002018, 2000016}; // filler' up
      int[] uncommons = {1000025, 1002662, 1002812, 1002850, 1002881, 1002880, 1012072, 4020009, 2043220, 2043022, 2040543, 2044420, 2040943, 2043713, 2044220, 2044120, 2040429, 2043220, 2040943}; // filler' up too
      int[] rares = {1002859, 1002553, 1002762, 1002763, 1002764, 1002765, 1002766, 1002663, 1002788, 1002949, 2049100, 2340000, 2040822, 2040822, 2040822, 2040822}; // filler' up last

      if (rand >= 25) {
         return commons[(int) (commons.length * Math.random())];
      } else if (rand <= 7 && rand >= 4) {
         return uncommons[(int) (uncommons.length * Math.random())];
      } else {
         return rares[(int) (rares.length * Math.random())];
      }
   }

   private static void debugFishingLikelihood() {
      long[] a = new long[365];
      long[] b = new long[365];
      long hits = 0, hits10 = 0, total = 0;

      for (int i = 0; i < 365; i++) {
         double yearLikelihood = getFishingLikelihood(i);

         int dayHits = 0, dayHits10 = 0;
         for (int k = 0; k < 24; k++) {
            for (int l = 0; l < 60; l++) {
               for (int m = 0; m < 60; m++) {
                  double timeLikelihood = getFishingLikelihood(k + l + m);

                  if ((0.23 * yearLikelihood) + (0.77 * timeLikelihood) > 57.777) {
                     hits++;
                     dayHits++;
                  }

                  if ((0.23 * yearLikelihood) + (0.77 * timeLikelihood) + 10.0 > 57.777) {
                     hits10++;
                     dayHits10++;
                  }

                  total++;
               }
            }
         }

         a[i] = dayHits;
         b[i] = dayHits10;
      }

      long maxHit = 0, minHit = Long.MAX_VALUE;
      for (int i = 0; i < 365; i++) {
         if (maxHit < a[i]) {
            maxHit = a[i];
         }

         if (minHit > a[i]) {
            minHit = a[i];
         }
      }

      long maxHit10 = 0, minHit10 = Long.MAX_VALUE;
      for (int i = 0; i < 365; i++) {
         if (maxHit10 < b[i]) {
            maxHit10 = b[i];
         }

         if (minHit10 > b[i]) {
            minHit10 = b[i];
         }
      }

      System.out.println("Diary   min " + minHit + " max " + maxHit);
      System.out.println("Diary10 min " + minHit10 + " max " + maxHit10);
      System.out.println("Hits: " + hits + "Hits10: " + hits10 + " Total: " + total + "   --  %1000: " + (hits * 1000 / total) + ", +10 %1000: " + (hits10 * 1000 / total));
   }
} 
