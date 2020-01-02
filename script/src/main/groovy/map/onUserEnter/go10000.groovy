package map.onUserEnter


import scripting.map.MapScriptMethods

class MapGo10000 {

   static def start(MapScriptMethods ms) {
      ms.unlockUI()
      ms.mapEffect("maplemap/enter/10000")
   }
}

MapGo10000 getMap() {
   getBinding().setVariable("map", new MapGo10000())
   return (MapGo10000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}