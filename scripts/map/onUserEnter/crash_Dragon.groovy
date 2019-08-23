package map.onUserEnter


import scripting.map.MapScriptMethods

class Mapcrash_Dragon {

   static def start(MapScriptMethods ms) {
      ms.lockUI()
      ms.showIntro("Effect/Direction4.img/crash/Scene" + ms.getPlayer().getGender())
   }
}

Mapcrash_Dragon getMap() {
   getBinding().setVariable("map", new Mapcrash_Dragon())
   return (Mapcrash_Dragon) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}