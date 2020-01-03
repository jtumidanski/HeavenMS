package reactor


import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Reactor6829000 extends SimpleReactor {
   def act() {
      MessageBroadcaster.getInstance().sendServerNotice(rm.getPlayer(), ServerNoticeType.PINK_TEXT, "Enjoy Halloween!")
      rm.spawnMonster(9400202, 10)
   }
}

Reactor6829000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6829000(rm: rm))
   return (Reactor6829000) getBinding().getVariable("reactor")
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