package map.onFirstUserEnter


import scripting.map.MapScriptMethods

class MapSpaceGaGasMap {

   static def start(MapScriptMethods ms) {
      ms.getPlayer().resetEnteredScript()
      ms.spawnMonster(9300331, -28, 0)
   }
}

MapSpaceGaGasMap getMap() {
   getBinding().setVariable("map", new MapSpaceGaGasMap())
   return (MapSpaceGaGasMap) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}