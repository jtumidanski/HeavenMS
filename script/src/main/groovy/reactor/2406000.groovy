package reactor


import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType


class Reactor2406000 {
   ReactorActionManager rm

   def act() {
      rm.spawnNpc(2081008)
      rm.startQuest(100203)
      MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.LIGHT_BLUE, "In a flicker of light the egg has matured and cracked, thus born a radiant baby dragon.")
   }

   def hit() {

   }

   def touch() {
      if (rm.haveItem(4001094) && rm.getReactor().getState() == ((byte) 0)) {
         rm.hitReactor()
         rm.gainItem(4001094, (short) -1)
      }
   }

   def release() {

   }
}

Reactor2406000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2406000(rm: rm))
   return (Reactor2406000) getBinding().getVariable("reactor")
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

def release() {
   getReactor().release()
}