package reactor


import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Reactor2110000 extends SimpleReactor {
   def act() {
      MessageBroadcaster.getInstance().sendServerNotice(rm.getPlayer(), ServerNoticeType.PINK_TEXT, "An unknown force has moved you to the starting point.")
      rm.warp(280010000, 0)
   }
}

Reactor2110000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2110000(rm: rm))
   return (Reactor2110000) getBinding().getVariable("reactor")
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