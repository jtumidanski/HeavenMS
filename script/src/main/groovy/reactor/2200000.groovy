package reactor


import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Reactor2200000 extends SimpleReactor {
   def act() {
      MessageBroadcaster.getInstance().sendServerNotice(rm.getPlayer(), ServerNoticeType.PINK_TEXT, "Gotcha! Try again next time!")
      rm.warp(221023200)
   }
}

Reactor2200000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2200000(rm: rm))
   return (Reactor2200000) getBinding().getVariable("reactor")
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