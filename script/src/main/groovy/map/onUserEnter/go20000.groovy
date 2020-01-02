package map.onUserEnter


import scripting.map.MapScriptMethods

class MapGo20000 {

   static def start(MapScriptMethods ms) {
      ms.unlockUI()
      ms.mapEffect("maplemap/enter/20000")
   }
}

MapGo20000 getMap() {
   getBinding().setVariable("map", new MapGo20000())
   return (MapGo20000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}