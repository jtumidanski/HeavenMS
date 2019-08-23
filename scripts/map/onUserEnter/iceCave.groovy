package map.onUserEnter


import scripting.map.MapScriptMethods

class MapiceCave {

   static def start(MapScriptMethods ms) {
      ms.teachSkill(20000014, (byte) -1, (byte) 0, -1)
      ms.teachSkill(20000015, (byte) -1, (byte) 0, -1)
      ms.teachSkill(20000016, (byte) -1, (byte) 0, -1)
      ms.teachSkill(20000017, (byte) -1, (byte) 0, -1)
      ms.teachSkill(20000018, (byte) -1, (byte) 0, -1)
      ms.unlockUI()
      ms.showIntro("Effect/Direction1.img/aranTutorial/ClickLilin")
   }
}

MapiceCave getMap() {
   getBinding().setVariable("map", new MapiceCave())
   return (MapiceCave) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}