package map.onUserEnter


import scripting.map.MapScriptMethods

class MapGoAdventure {

   static def start(MapScriptMethods ms) {
      ms.goAdventure()
   }
}

MapGoAdventure getMap() {
   getBinding().setVariable("map", new MapGoAdventure())
   return (MapGoAdventure) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}