package map.onUserEnter


import scripting.map.MapScriptMethods

class MapGo1020000 {

   static def start(MapScriptMethods ms) {
      ms.unlockUI()
      ms.mapEffect("maplemap/enter/1020000")
   }
}

MapGo1020000 getMap() {
   getBinding().setVariable("map", new MapGo1020000())
   return (MapGo1020000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}