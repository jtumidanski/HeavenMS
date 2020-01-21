package reactor


import scripting.reactor.ReactorActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Reactor2201003 extends SimpleReactor {
   def act() {
      if (rm.getPlayer().getMapId() == 922010900) {
         MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, I18nMessage.from("ALISHAR_SUMMONED"))
         rm.spawnMonster(9300012, 941, 184)
      } else if (rm.getPlayer().getMapId() == 922010700) {
         MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, I18nMessage.from("ROMBARD_SUMMONED"))
         rm.spawnMonster(9300010, 1, -211)
      }
   }
}

Reactor2201003 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2201003(rm: rm))
   return (Reactor2201003) getBinding().getVariable("reactor")
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