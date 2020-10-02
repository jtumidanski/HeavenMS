package tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import server.MapleItemInformationProvider;
import server.MapleTrade;
import server.expeditions.MapleExpedition;

public class LogHelper {

   public static void logTrade(MapleTrade trade1, MapleTrade trade2) {
      String name1 = trade1.getOwner().getName();
      String name2 = trade2.getOwner().getName();
      StringBuilder log = new StringBuilder("TRADE BETWEEN " + name1 + " AND " + name2 + "\r\n");
      //Trade 1 to trade 2
      logTrade(trade1, name1, name2, log);
      //Trade 2 to trade 1
      logTrade(trade2, name2, name1, log);
      log.append("\r\n\r\n");
      LoggerUtil.printInfo(LoggerOriginator.LOG_TRADE, log.toString());
   }

   protected static void logTrade(MapleTrade trade1, String name1, String name2, StringBuilder log) {
      log.append(trade1.getExchangeMesos()).append(" mesos from ").append(name1).append(" to ").append(name2).append(" \r\n");
      for (Item item : trade1.getItems()) {
         String itemName = MapleItemInformationProvider.getInstance().getName(item.id()) + "(" + item.id() + ")";
         log.append(item.quantity()).append(" ").append(itemName).append(" from ").append(name1).append(" to ").append(name2).append(" \r\n");
      }
   }

   public static void logExpedition(MapleExpedition expedition) {
      MessageBroadcaster.getInstance().sendWorldServerNotice(expedition.getLeader().getWorld(), ServerNoticeType.LIGHT_BLUE, MapleCharacter::isGM, I18nMessage.from("EXPEDITION_ENDED").with(expedition.getType().toString(), expedition.getLeader().getName(), getTimeString(expedition.getStartTime())));

      StringBuilder log = new StringBuilder(expedition.getType().toString() + " EXPEDITION\r\n");
      log.append(getTimeString(expedition.getStartTime())).append("\r\n");

      for (String memberName : expedition.getMembers().values()) {
         log.append(">>").append(memberName).append("\r\n");
      }
      log.append("BOSS KILLS\r\n");
      for (String message : expedition.getBossLogs()) {
         log.append(message);
      }
      log.append("\r\n");
      LoggerUtil.printInfo(LoggerOriginator.LOG_EXPEDITION, log.toString());
   }

   public static String getTimeString(long then) {
      long duration = System.currentTimeMillis() - then;
      int seconds = (int) (duration / 1000) % 60;
      int minutes = (int) ((duration / (1000 * 60)) % 60);
      return minutes + " Minutes and " + seconds + " Seconds";
   }

   public static void logLeaf(MapleCharacter player, boolean gotPrize, String operation) {
      String timeStamp = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date());
      String log = player.getName() + (gotPrize ? " used a maple leaf to buy " + operation : " redeemed " + operation + " VP for a leaf") + " - " + timeStamp;
      LoggerUtil.printInfo(LoggerOriginator.LOG_LEAF, log);
   }

   public static void logGachapon(MapleCharacter player, int itemId, String map) {
      String itemName = MapleItemInformationProvider.getInstance().getName(itemId);
      String timeStamp = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date());
      String log = player.getName() + " got a " + itemName + "(" + itemId + ") from the " + map + " gachapon. - " + timeStamp;
      LoggerUtil.printInfo(LoggerOriginator.LOG_GACHAPON, log);
   }

   public static void logChat(MapleClient player, String chatType, String text) {
      SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
      LoggerUtil.printInfo(LoggerOriginator.LOG_CHAT, "[" + sdf.format(Calendar.getInstance().getTime()) + "] (" + chatType + ") " + player.getPlayer().getName() + ": " + text);
   }

}
