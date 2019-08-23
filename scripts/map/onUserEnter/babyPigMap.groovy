package map.onUserEnter


import scripting.map.MapScriptMethods

class MapbabyPigMap {

   static def start(MapScriptMethods ms) {
      ms.unlockUI()
      ms.getClient().getQM().forceStartQuest(22015)
   }
}

MapbabyPigMap getMap() {
   getBinding().setVariable("map", new MapbabyPigMap())
   return (MapbabyPigMap) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}