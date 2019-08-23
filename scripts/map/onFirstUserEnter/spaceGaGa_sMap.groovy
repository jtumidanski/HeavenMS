package map.onFirstUserEnter


import scripting.map.MapScriptMethods

class MapspaceGaGa_sMap {

   static def start(MapScriptMethods ms) {
      ms.getPlayer().resetEnteredScript()
      ms.spawnMonster(9300331, -28, 0)
   }
}

MapspaceGaGa_sMap getMap() {
   getBinding().setVariable("map", new MapspaceGaGa_sMap())
   return (MapspaceGaGa_sMap) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}