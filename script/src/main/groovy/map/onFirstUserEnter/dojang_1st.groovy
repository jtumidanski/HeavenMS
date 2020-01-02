package map.onFirstUserEnter

import scripting.map.MapScriptMethods

class MapDojang_1st {

   static def start(MapScriptMethods ms) {
      ms.getPlayer().resetEnteredScript()
      int stage = Math.floor(ms.getMapId() / 100) % 100

      if (stage % 6 == 0) {
         ms.getClient().getChannelServer().dismissDojoSchedule(ms.getMapId(), ms.getParty().orElseThrow())
         ms.getClient().getChannelServer().setDojoProgress(ms.getMapId())
      } else {
         boolean callBoss = ms.getClient().getChannelServer().setDojoProgress(ms.getMapId())

         int realStage = stage - ((stage / 6) | 0)
         ms.getMonsterLifeFactory(9300183 + realStage).ifPresent({ mob ->
            if (callBoss && ms.getPlayer().getMap().getMonsterById(9300216) == null) {
               mob.setBoss(false)
               ms.getPlayer().getMap().spawnDojoMonster(mob)
            }
         })
      }
   }
}

MapDojang_1st getMap() {
   getBinding().setVariable("map", new MapDojang_1st())
   return (MapDojang_1st) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}