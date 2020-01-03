package reactor


import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Reactor2221001 extends SimpleReactor {
   def act() {
      rm.spawnMonster(7130401)
      MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, "Here comes Blue King Goblin!")
   }
}

Reactor2221001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2221001(rm: rm))
   return (Reactor2221001) getBinding().getVariable("reactor")
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