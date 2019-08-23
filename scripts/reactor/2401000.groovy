package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager

import java.awt.*


class Reactor2401000 {
   ReactorActionManager rm

   def act() {
      rm.changeMusic("Bgm14/HonTale")
      if (rm.getReactor().getMap().getMonsterById(8810026) == null) {
         rm.getReactor().getMap().spawnHorntailOnGroundBelow(new Point(71, 260))

         EventInstanceManager eim = rm.getEventInstance()
         eim.restartEventTimer(60 * 60000)
      }
      rm.mapMessage(6, "From the depths of his cave, here comes Horntail!")
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2401000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2401000(rm: rm))
   return (Reactor2401000) getBinding().getVariable("reactor")
}

def act() {
   getReactor().act()
}

def hit() {
   getReactor().hit()
}

def touch() {
   getReactor().touch()
}

def untouch() {
   getReactor().untouch()
}