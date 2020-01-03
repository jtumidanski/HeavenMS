package reactor


import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Reactor2221002 extends SimpleReactor {
   def act() {
      rm.spawnMonster(7130402, -340, 100)
      MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, "Here comes Green King Goblin!")
   }
}

Reactor2221002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2221002(rm: rm))
   return (Reactor2221002) getBinding().getVariable("reactor")
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