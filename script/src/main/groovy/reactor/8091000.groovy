package reactor


import scripting.reactor.ReactorActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Reactor8091000 extends SimpleReactor {
   def act() {
      rm.spawnMonster(9400210, 2)
      rm.spawnMonster(9400209, 2)
      MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, I18nMessage.from("SOME_MONSTERS_SUMMONED"))
   }
}

Reactor8091000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor8091000(rm: rm))
   return (Reactor8091000) getBinding().getVariable("reactor")
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