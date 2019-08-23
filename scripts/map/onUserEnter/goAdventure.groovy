package map.onUserEnter


import scripting.map.MapScriptMethods

class MapgoAdventure {

   static def start(MapScriptMethods ms) {
      ms.goAdventure()
   }
}

MapgoAdventure getMap() {
   getBinding().setVariable("map", new MapgoAdventure())
   return (MapgoAdventure) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}