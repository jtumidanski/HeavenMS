package map.onUserEnter


import scripting.map.MapScriptMethods

class MapPromiseDragon {

   static def start(MapScriptMethods ms) {
      ms.lockUI()
      ms.showIntro("Effect/Direction4.img/PromiseDragon/Scene" + ms.getPlayer().getGender())
   }
}

MapPromiseDragon getMap() {
   getBinding().setVariable("map", new MapPromiseDragon())
   return (MapPromiseDragon) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}