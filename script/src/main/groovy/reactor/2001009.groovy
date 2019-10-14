package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager

import java.awt.*


class Reactor2001009 {
   ReactorActionManager rm

   def act() {
      if (rm.getEventInstance().getIntProperty("statusStg2") == -1) {
         int rnd = Math.floor(Math.random() * 14).intValue()

         rm.getEventInstance().setProperty("statusStg2", "" + rnd)
         rm.getEventInstance().setProperty("statusStg2_c", "0")
      }

      int limit = rm.getEventInstance().getIntProperty("statusStg2")
      int count = rm.getEventInstance().getIntProperty("statusStg2_c")
      if (count >= limit) {
         rm.dropItems()

         EventInstanceManager eim = rm.getEventInstance()
         eim.giveEventPlayersExp(3500)

         eim.setProperty("statusStg2", "1")
         eim.showClearEffect(true)
      } else {
         count++
         rm.getEventInstance().setProperty("statusStg2_c", count)

         int nextHashed = (11 * (count)) % 14

         Point nextPos = rm.getMap().getReactorById(2001002 + nextHashed).position()
         rm.spawnMonster(9300040, 1, nextPos)
      }
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2001009 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2001009(rm: rm))
   return (Reactor2001009) getBinding().getVariable("reactor")
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