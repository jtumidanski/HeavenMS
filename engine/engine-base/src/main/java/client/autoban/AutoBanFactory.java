package client.autoban;

import client.MapleCharacter;
import config.YamlConfig;
import net.server.Server;
import tools.FilePrinter;
import tools.MapleLogger;
import tools.PacketCreator;
import tools.StringUtil;
import tools.packet.message.YellowTip;

public enum AutoBanFactory {
   MOB_COUNT,
   GENERAL,
   FIX_DAMAGE,
   DAMAGE_HACK(15, 60 * 1000),
   DISTANCE_HACK(10, 120 * 1000),
   PORTAL_DISTANCE(5, 30000),
   PACKET_EDIT,
   ACC_HACK,
   CREATION_GENERATOR,
   HIGH_HP_HEALING,
   FAST_HP_HEALING(15),
   FAST_MP_HEALING(20, 30000),
   GACHAPON_EXP,
   TUBI(20, 15000),
   SHORT_ITEM_VAC,
   ITEM_VAC,
   FAST_ITEM_PICKUP(5, 30000),
   FAST_ATTACK(10, 30000),
   MPCON(25, 30000);

   private int points;
   private long expireTime;

   AutoBanFactory() {
      this(1, -1);
   }

   AutoBanFactory(int points) {
      this.points = points;
      this.expireTime = -1;
   }

   AutoBanFactory(int points, long expire) {
      this.points = points;
      this.expireTime = expire;
   }

   public int getMaximum() {
      return points;
   }

   public long getExpire() {
      return expireTime;
   }

   public void addPoint(AutoBanManager ban, String reason) {
      ban.addPoint(this, reason);
   }

   public void alert(MapleCharacter chr, String reason) {
      if (YamlConfig.config.server.USE_AUTOBAN) {
         if (chr != null && MapleLogger.ignored.contains(chr.getId())) {
            return;
         }
         Server.getInstance().broadcastGMMessage((chr != null ? chr.getWorld() : 0), PacketCreator.create(new YellowTip((chr != null ? StringUtil.makeMapleReadable(chr.getName()) : "") + " caused " + this.name() + " " + reason)));
      }
      if (YamlConfig.config.server.USE_AUTOBAN_LOG) {
         FilePrinter.print(FilePrinter.AUTOBAN_WARNING, (chr != null ? StringUtil.makeMapleReadable(chr.getName()) : "") + " caused " + this.name() + " " + reason);
      }
   }

   public void autoBan(MapleCharacter chr, String value) {
      if (YamlConfig.config.server.USE_AUTOBAN) {
         chr.autoBan("Auto banned for (" + this.name() + ": " + value + ")");
         //chr.sendPolice("You will be disconnected for (" + this.name() + ": " + value + ")");
      }
   }
}
