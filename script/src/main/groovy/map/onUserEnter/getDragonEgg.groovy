package map.onUserEnter


import scripting.map.MapScriptMethods

class MapGetDragonEgg {

   static def start(MapScriptMethods ms) {
      ms.lockUI()
      ms.showIntro("Effect/Direction4.img/getDragonEgg/Scene" + ms.getPlayer().getGender())
   }
}

MapGetDragonEgg getMap() {
   getBinding().setVariable("map", new MapGetDragonEgg())
   return (MapGetDragonEgg) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}