package server.events.gm;

import java.util.concurrent.ScheduledFuture;

import client.MapleCharacter;
import server.TimerManager;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.ui.GetClock;

public class MapleFitness {
   private MapleCharacter chr;
   private long time = 0;
   private long timeStarted = 0;
   private ScheduledFuture<?> schedule;
   private ScheduledFuture<?> scheduledMessage = null;

   public MapleFitness(final MapleCharacter chr) {
      this.chr = chr;
      this.schedule = TimerManager.getInstance().schedule(() -> {
         if (chr.getMapId() >= 109040000 && chr.getMapId() <= 109040004) {
            chr.changeMap(chr.getMap().getReturnMap());
         }
      }, 900000);
   }

   public void startFitness() {
      chr.getMap().startEvent();
      PacketCreator.announce(chr, new GetClock(900));
      this.timeStarted = System.currentTimeMillis();
      this.time = 900000;
      checkAndMessage();

      chr.getMap().getPortal("join00").setPortalStatus(true);
      MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, I18nMessage.from("EVENT_FITNESS_START"));
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
      scheduledMessage.cancel(false);
   }

   public long getTimeLeft() {
      return time - (System.currentTimeMillis() - timeStarted);
   }

   public void checkAndMessage() {
      this.scheduledMessage = TimerManager.getInstance().register(() -> {
         if (chr.getFitness() == null) {
            resetTimes();
         }
         if (chr.getMap().getId() >= 109040000 && chr.getMap().getId() <= 109040004) {
            if (getTimeLeft() > 9000 && getTimeLeft() < 11000) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, I18nMessage.from("EVENT_FITNESS_TIME_WARNING_1"));
            } else if (getTimeLeft() > 99000 && getTimeLeft() < 101000) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, I18nMessage.from("EVENT_FITNESS_TIME_WARNING_2"));
            } else if (getTimeLeft() > 239000 && getTimeLeft() < 241000) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, I18nMessage.from("EVENT_FITNESS_TIME_WARNING_3"));
            } else if (getTimeLeft() > 299000 && getTimeLeft() < 301000) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, I18nMessage.from("EVENT_FITNESS_TIME_WARNING_4"));
            } else if (getTimeLeft() > 359000 && getTimeLeft() < 361000) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, I18nMessage.from("EVENT_FITNESS_TIME_WARNING_5"));
            } else if (getTimeLeft() > 499000 && getTimeLeft() < 501000) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, I18nMessage.from("EVENT_FITNESS_TIME_WARNING_6"));
            } else if (getTimeLeft() > 599000 && getTimeLeft() < 601000) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, I18nMessage.from("EVENT_FITNESS_TIME_WARNING_7"));
            } else if (getTimeLeft() > 659000 && getTimeLeft() < 661000) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, I18nMessage.from("EVENT_FITNESS_TIME_WARNING_8"));
            } else if (getTimeLeft() > 699000 && getTimeLeft() < 701000) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, I18nMessage.from("EVENT_FITNESS_TIME_WARNING_9"));
            } else if (getTimeLeft() > 779000 && getTimeLeft() < 781000) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, I18nMessage.from("EVENT_FITNESS_TIME_WARNING_10"));
            } else if (getTimeLeft() > 839000 && getTimeLeft() < 841000) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, I18nMessage.from("EVENT_FITNESS_TIME_WARNING_11"));
            } else if (getTimeLeft() > 869000 && getTimeLeft() < 871000) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, I18nMessage.from("EVENT_FITNESS_TIME_WARNING_12"));
            }
         } else {
            resetTimes();
         }
      }, 5000, 29500);
   }
   // 14:30 [Notice][MapleStory Physical Fitness Test] consists of 4 stages, and if you happen to die during the game, you'll be eliminated from the game, so please be careful of that.
   // 14:00 [Notice]There may be a heavy lag due to many users at stage 1 all at once. It won't be difficult, so please make sure not to fall down because of heavy lag.
   // 13:00 [Notice]Everyone that clears [The Maple Physical Fitness Test] on time will be given an item, regardless of the order of finish, so just relax, take your time, and clear the 4 stages.
   // 11:40 [Notice]Please remember that if you die during the event, you'll be eliminated from the game. You still have plenty of time left, so either take a potion or recover HP first before moving on.
   // 11:00 [Notice]The 2nd stage offers monkeys throwing bananas. Please make sure to avoid them by moving along at just the right timing.
   // 10:00 [Notice]The most important thing you'll need to know to avoid the bananas thrown by the monkeys is *Timing* Timing is everything in this!
   // 8:20 [Notice]Please remember that if you die during the event, you'll be eliminated from the game. If you're running out of HP, either take a potion or recover HP first before moving on.
   // 6:00 [Notice]For those who have heavy lags, please make sure to move slowly to avoid falling all the way down because of lags.
   // 5:00 [Notice]The 3rd stage offers traps where you may see them, but you won't be able to step on them. Please be careful of them as you make your way up.
   // 4:00 [Notice]The 4th stage is the last one for [The Maple Physical Fitness Test]. Please don't give up at the last minute and try your best. The reward is waiting for you at the very top!
   // 1:40 [Notice]Alright, you don't have much time remaining. Please hurry up a little!
   // 0:10 [Notice]You have 10 sec left. Those of you unable to beat the game, we hope you beat it next time! Great job everyone!! See you later~
}
