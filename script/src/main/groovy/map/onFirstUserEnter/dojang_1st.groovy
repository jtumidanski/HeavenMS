package map.onFirstUserEnter


import scripting.map.MapScriptMethods
import server.life.MapleMonster

class Mapdojang_1st {

   static def start(MapScriptMethods ms) {
      ms.getPlayer().resetEnteredScript()
      int stage = Math.floor(ms.getMapId() / 100) % 100

      if (stage % 6 == 1) {
         ms.getClient().getChannelServer().startDojoSchedule(ms.getMapId())
      } else if (stage % 6 == 0) {
         ms.getClient().getChannelServer().dismissDojoSchedule(ms.getMapId(), ms.getParty().orElseThrow())
      }

      boolean callBoss = ms.getClient().getChannelServer().setDojoProgress(ms.getMapId())

      if (stage % 6 > 0) {
         int realstage = stage - ((stage / 6) | 0)
         MapleMonster mob = ms.getMonsterLifeFactory(9300183 + realstage)
         if (callBoss && mob != null && ms.getPlayer().getMap().getMonsterById(9300216) == null) {
            mob.setBoss(false)
            ms.getPlayer().getMap().spawnDojoMonster(mob)
         }
      }
   }
}

Mapdojang_1st getMap() {
   getBinding().setVariable("map", new Mapdojang_1st())
   return (Mapdojang_1st) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}