package reactor


import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Reactor1200000 extends SimpleReactor {
   def act() {
      MessageBroadcaster.getInstance().sendServerNotice(rm.getPlayer(), ServerNoticeType.PINK_TEXT, "Failed to find Bart. Returning to the original location.")
      rm.warp(120000102)
   }
}

Reactor1200000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor1200000(rm: rm))
   return (Reactor1200000) getBinding().getVariable("reactor")
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