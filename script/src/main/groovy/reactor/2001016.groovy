package reactor


import scripting.reactor.ReactorActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Reactor2001016 extends SimpleReactor {
   def act() {
      rm.getMap().killAllMonsters()
      rm.getMap().allowSummonState(false)
      rm.spawnMonster(9300039, 260, 490)
      MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, I18nMessage.from("2001016_AS_THE_AIR"))
   }
}

Reactor2001016 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2001016(rm: rm))
   return (Reactor2001016) getBinding().getVariable("reactor")
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