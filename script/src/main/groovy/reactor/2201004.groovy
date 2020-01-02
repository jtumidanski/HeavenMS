package reactor


import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType


class Reactor2201004 {
   ReactorActionManager rm

   def act() {
      MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, "The dimensional hole has been filled by the <Piece of Cracked Dimension>.")
      rm.changeMusic("Bgm09/TimeAttack")
      rm.spawnMonster(8500000, -410, -400)
      rm.createMapMonitor(220080001, "in00")
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor2201004 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2201004(rm: rm))
   return (Reactor2201004) getBinding().getVariable("reactor")
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