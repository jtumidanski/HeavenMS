package map.onUserEnter


import scripting.map.MapScriptMethods

class MapBabyPigMap {

   static def start(MapScriptMethods ms) {
      ms.unlockUI()
      ms.getClient().getQM().forceStartQuest(22015)
   }
}

MapBabyPigMap getMap() {
   getBinding().setVariable("map", new MapBabyPigMap())
   return (MapBabyPigMap) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}