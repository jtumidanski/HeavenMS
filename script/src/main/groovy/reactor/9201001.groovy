package reactor


import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType


class Reactor9201001 {
   ReactorActionManager rm

   def act() {
      MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, "A bright flash of light, then someone familiar appears in front of the blocked gate.")
      rm.spawnNpc(9040003)
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor9201001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9201001(rm: rm))
   return (Reactor9201001) getBinding().getVariable("reactor")
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