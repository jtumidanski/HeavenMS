package server.life;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;

import client.MapleCharacter;
import net.server.Server;

public class SpawnPoint {
   private int monster, mobTime, team, fh, f;
   private Point pos;
   private long nextPossibleSpawn;
   private int mobInterval;
   private AtomicInteger spawnedMonsters = new AtomicInteger(0);
   private boolean immobile, denySpawn = false;

   public SpawnPoint(final MapleMonster monster, Point pos, boolean immobile, int mobTime, int mobInterval, int team) {
      this.monster = monster.id();
      this.pos = new Point(pos);
      this.mobTime = mobTime;
      this.team = team;
      this.fh = monster.fh();
      this.f = monster.f();
      this.immobile = immobile;
      this.mobInterval = mobInterval;
      this.nextPossibleSpawn = Server.getInstance().getCurrentTime();
   }

   public int getSpawned() {
      return spawnedMonsters.intValue();
   }

   public boolean getDenySpawn() {
      return denySpawn;
   }

   public void setDenySpawn(boolean val) {
      denySpawn = val;
   }

   public boolean shouldSpawn() {
      if (denySpawn || mobTime < 0 || spawnedMonsters.get() > 0) {
         return false;
      }
      return nextPossibleSpawn <= Server.getInstance().getCurrentTime();
   }

   public boolean shouldForceSpawn() {
      return mobTime >= 0 && spawnedMonsters.get() <= 0;
   }

   public MapleMonster getMonster() {
      MapleMonster mob = new MapleMonster(MapleLifeFactory.getMonster(monster).orElseThrow());
      mob.setPosition(new Point(pos));
      mob.setTeam(team);
      mob.setFh(fh);
      mob.setF(f);
      spawnedMonsters.incrementAndGet();
      mob.addListener(new MonsterListener() {
         @Override
         public void monsterKilled(int aniTime) {
            nextPossibleSpawn = Server.getInstance().getCurrentTime();
            if (mobTime > 0) {
               nextPossibleSpawn += mobTime * 1000;
            } else {
               nextPossibleSpawn += aniTime;
            }
            spawnedMonsters.decrementAndGet();
         }

         @Override
         public void monsterDamaged(MapleCharacter from, int trueDmg) {
         }

         @Override
         public void monsterHealed(int trueHeal) {
         }
      });
      if (mobTime == 0) {
         nextPossibleSpawn = Server.getInstance().getCurrentTime() + mobInterval;
      }
      return mob;
   }

   public int getMonsterId() {
      return monster;
   }

   public Point getPosition() {
      return pos;
   }

   public final int getF() {
      return f;
   }

   public final int getFh() {
      return fh;
   }

   public int getMobTime() {
      return mobTime;
   }

   public int getTeam() {
      return team;
   }
}
