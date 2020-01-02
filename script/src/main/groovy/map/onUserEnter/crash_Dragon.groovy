package map.onUserEnter


import scripting.map.MapScriptMethods

class MapCrashDragon {

   static def start(MapScriptMethods ms) {
      ms.lockUI()
      ms.showIntro("Effect/Direction4.img/crash/Scene" + ms.getPlayer().getGender())
   }
}

MapCrashDragon getMap() {
   getBinding().setVariable("map", new MapCrashDragon())
   return (MapCrashDragon) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}