package map.onUserEnter


import scripting.map.MapScriptMethods

class MapResi_tutor30 {

   static def start(MapScriptMethods ms) {
      ms.updateAreaInfo((short) 23007, "exp1=1;exp2=1;exp3=1;exp4=1")//force
      ms.showInfo("Effect/OnUserEff.img/guideEffect/resistanceTutorial/userTalk")
   }
}

MapResi_tutor30 getMap() {
   getBinding().setVariable("map", new MapResi_tutor30())
   return (MapResi_tutor30) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}