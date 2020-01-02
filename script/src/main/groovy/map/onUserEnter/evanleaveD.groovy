package map.onUserEnter


import scripting.map.MapScriptMethods

class MapEvanLeaveD {

   static def start(MapScriptMethods ms) {
      ms.unlockUI()
   }
}

MapEvanLeaveD getMap() {
   getBinding().setVariable("map", new MapEvanLeaveD())
   return (MapEvanLeaveD) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}