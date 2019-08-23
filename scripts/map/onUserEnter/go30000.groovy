package map.onUserEnter


import scripting.map.MapScriptMethods

class Mapgo30000 {

   static def start(MapScriptMethods ms) {
      ms.mapEffect("maplemap/enter/30000")
   }
}

Mapgo30000 getMap() {
   getBinding().setVariable("map", new Mapgo30000())
   return (Mapgo30000) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}