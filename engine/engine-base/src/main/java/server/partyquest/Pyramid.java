package server.partyquest;

import java.util.concurrent.ScheduledFuture;

import client.MapleCharacter;
import net.server.world.MapleParty;
import server.MapleItemInformationProvider;
import server.TimerManager;
import tools.PacketCreator;
import tools.packet.GetEnergy;
import tools.packet.pyramid.PyramidGauge;
import tools.packet.pyramid.PyramidScore;

public class Pyramid extends PartyQuest {
   int kill = 0, miss = 0, cool = 0, exp = 0, map, count;
   byte coolAdd = 5, missSub = 4, decrease = 1;
   short gauge;
   byte rank, skill = 0, stage = 0, buffCount = 0;//buffCount includes buffs + skills
   PyramidMode mode;
   ScheduledFuture<?> timer = null;
   ScheduledFuture<?> gaugeSchedule = null;

   public Pyramid(MapleParty party, PyramidMode mode, int mapId) {
      super(party);
      this.mode = mode;
      this.map = mapId;

      byte plus = (byte) mode.getMode();
      coolAdd += plus;
      missSub += plus;
      switch (plus) {
         case 0:
            decrease = 1;
         case 1:
         case 2:
            decrease = 2;
         case 3:
            decrease = 3;
      }
   }

   public void startGaugeSchedule() {
      if (gaugeSchedule == null) {
         gauge = 100;
         count = 0;
         gaugeSchedule = TimerManager.getInstance().register(() -> {
            gauge -= decrease;
            if (gauge <= 0) {
               warp(926010001);
            }
         }, 1000);
      }
   }

   public void kill() {
      kill++;
      if (gauge < 100) {
         count++;
      }
      gauge++;
      broadcastInfo("hit", kill);
      if (gauge >= 100) {
         gauge = 100;
      }
      checkBuffs();
   }

   public void cool() {
      cool++;
      int plus = coolAdd;
      if ((gauge + coolAdd) > 100) {
         plus -= ((gauge + coolAdd) - 100);
      }
      gauge += plus;
      count += plus;
      if (gauge >= 100) {
         gauge = 100;
      }
      broadcastInfo("cool", cool);
      checkBuffs();

   }

   public void miss() {
      miss++;
      count -= missSub;
      gauge -= missSub;
      broadcastInfo("miss", miss);
   }

   public int timer() {
      int value;
      if (stage > 0) {
         value = 180;
      } else {
         value = 120;
      }

      timer = TimerManager.getInstance().schedule(() -> {
         stage++;
         warp(map + (stage * 100));//Should work :D
      }, value * 1000);//, 4000
      broadcastInfo("party", getParticipants().size() > 1 ? 1 : 0);
      broadcastInfo("hit", kill);
      broadcastInfo("miss", miss);
      broadcastInfo("cool", cool);
      broadcastInfo("skill", skill);
      broadcastInfo("laststage", stage);
      startGaugeSchedule();
      return value;
   }

   public void warp(int mapId) {
      for (MapleCharacter chr : getParticipants()) {
         chr.changeMap(mapId, 0);
      }
      if (stage > -1) {
         gaugeSchedule.cancel(false);
         gaugeSchedule = null;
         timer.cancel(false);
         timer = null;
      } else {
         stage = 0;
      }
   }

   public void broadcastInfo(String info, int amount) {
      for (MapleCharacter chr : getParticipants()) {
         PacketCreator.announce(chr, new GetEnergy("massacre_" + info, amount));
         PacketCreator.announce(chr, new PyramidGauge(count));
      }
   }

   public boolean useSkill() {
      if (skill < 1) {
         return false;
      }

      skill--;
      broadcastInfo("skill", skill);
      return true;
   }

   public void checkBuffs() {
      int total = (kill + cool);
      if (buffCount == 0 && total >= 250) {
         buffCount++;
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         for (MapleCharacter chr : getParticipants()) {
            ii.getItemEffect(2022585).applyTo(chr);
         }

      } else if (buffCount == 1 && total >= 500) {
         buffCount++;
         skill++;
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         for (MapleCharacter chr : getParticipants()) {
            PacketCreator.announce(chr, new GetEnergy("massacre_skill", skill));
            ii.getItemEffect(2022586).applyTo(chr);
         }
      } else if (buffCount == 2 && total >= 1000) {
         buffCount++;
         skill++;
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         for (MapleCharacter chr : getParticipants()) {
            PacketCreator.announce(chr, new GetEnergy("massacre_skill", skill));
            ii.getItemEffect(2022587).applyTo(chr);
         }
      } else if (buffCount == 3 && total >= 1500) {
         skill++;
         broadcastInfo("skill", skill);
      } else if (buffCount == 4 && total >= 2000) {
         buffCount++;
         skill++;
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         for (MapleCharacter chr : getParticipants()) {
            PacketCreator.announce(chr, new GetEnergy("massacre_skill", skill));
            ii.getItemEffect(2022588).applyTo(chr);
         }
      } else if (buffCount == 5 && total >= 2500) {
         skill++;
         broadcastInfo("skill", skill);
      } else if (buffCount == 6 && total >= 3000) {
         skill++;
         broadcastInfo("skill", skill);
      }
   }

   public void sendScore(MapleCharacter chr) {
      if (exp == 0) {
         int totalKills = (kill + cool);
         if (stage == 5) {
            if (totalKills >= 3000) {
               rank = 0;
            } else if (totalKills >= 2000) {
               rank = 1;
            } else if (totalKills >= 1500) {
               rank = 2;
            } else if (totalKills >= 500) {
               rank = 3;
            } else {
               rank = 4;
            }
         } else {
            if (totalKills >= 2000) {
               rank = 3;
            } else {
               rank = 4;
            }
         }

         if (rank == 0) {
            exp = (60500 + (5500 * mode.getMode()));
         } else if (rank == 1) {
            exp = (55000 + (5000 * mode.getMode()));
         } else if (rank == 2) {
            exp = (46750 + (4250 * mode.getMode()));
         } else if (rank == 3) {
            exp = (22000 + (2000 * mode.getMode()));
         }

         exp += ((kill * 2) + (cool * 10));
      }
      PacketCreator.announce(chr, new PyramidScore(rank, exp));
      chr.gainExp(exp, true, true);
   }

   public enum PyramidMode {
      EASY(0), NORMAL(1), HARD(2), HELL(3);
      int mode;

      PyramidMode(int mode) {
         this.mode = mode;
      }

      public int getMode() {
         return mode;
      }
   }
}


