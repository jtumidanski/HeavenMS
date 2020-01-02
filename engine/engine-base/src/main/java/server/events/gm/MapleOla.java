package server.events.gm;

import java.util.concurrent.ScheduledFuture;

import client.MapleCharacter;
import server.TimerManager;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.ui.GetClock;

public class MapleOla {
   private MapleCharacter chr;
   private long time = 0;
   private long timeStarted = 0;
   private ScheduledFuture<?> schedule;

   public MapleOla(final MapleCharacter chr) {
      this.chr = chr;
      this.schedule = TimerManager.getInstance().schedule(() -> {
         if (chr.getMapId() >= 109030001 && chr.getMapId() <= 109030303) {
            chr.changeMap(chr.getMap().getReturnMap());
         }
         resetTimes();
      }, 360000);
   }

   public void startOla() { // TODO: Messages
      chr.getMap().startEvent();
      PacketCreator.announce(chr, new GetClock(360));
      this.timeStarted = System.currentTimeMillis();
      this.time = 360000;

      chr.getMap().getPortal("join00").setPortalStatus(true);
      MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, "The portal has now opened. Press the up arrow key at the portal to enter.");
   }

   public boolean isTimerStarted() {
      return time > 0 && timeStarted > 0;
   }

   public long getTime() {
      return time;
   }

   public void resetTimes() {
      this.time = 0;
      this.timeStarted = 0;
      schedule.cancel(false);
   }

   public long getTimeLeft() {
      return time - (System.currentTimeMillis() - timeStarted);
   }
}
