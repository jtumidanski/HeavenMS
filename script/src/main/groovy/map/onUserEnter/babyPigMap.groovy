package map.onUserEnter


import scripting.map.MapScriptMethods
import server.processor.QuestProcessor

class MapBabyPigMap {

   static def start(MapScriptMethods ms) {
      ms.unlockUI()
      QuestProcessor.getInstance().forceStartScript(ms.getClient().getPlayer().getId(), 22015)
   }
}

MapBabyPigMap getMap() {
   getBinding().setVariable("map", new MapBabyPigMap())
   return (MapBabyPigMap) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}