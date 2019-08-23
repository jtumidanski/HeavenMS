package map.onUserEnter


import scripting.map.MapScriptMethods

class MapevanleaveD {

   static def start(MapScriptMethods ms) {
      ms.unlockUI()
   }
}

MapevanleaveD getMap() {
   getBinding().setVariable("map", new MapevanleaveD())
   return (MapevanleaveD) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}