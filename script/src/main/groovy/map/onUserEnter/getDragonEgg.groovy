package map.onUserEnter


import scripting.map.MapScriptMethods

class MapgetDragonEgg {

   static def start(MapScriptMethods ms) {
      ms.lockUI()
      ms.showIntro("Effect/Direction4.img/getDragonEgg/Scene" + ms.getPlayer().getGender())
   }
}

MapgetDragonEgg getMap() {
   getBinding().setVariable("map", new MapgetDragonEgg())
   return (MapgetDragonEgg) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}