package reactor


import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Reactor2201003 extends SimpleReactor {
   def act() {
      if (rm.getPlayer().getMapId() == 922010900) {
         MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, "Alishar has been summoned.")
         rm.spawnMonster(9300012, 941, 184)
      } else if (rm.getPlayer().getMapId() == 922010700) {
         MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, "Rombard has been summoned somewhere in the map.")
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