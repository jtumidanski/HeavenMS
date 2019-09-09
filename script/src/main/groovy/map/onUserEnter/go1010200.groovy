package map.onUserEnter


import scripting.map.MapScriptMethods

class Mapgo1010200 {

   static def start(MapScriptMethods ms) {
      ms.mapEffect("maplemap/enter/1010200")
   }
}

Mapgo1010200 getMap() {
   getBinding().setVariable("map", new Mapgo1010200())
   return (Mapgo1010200) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}