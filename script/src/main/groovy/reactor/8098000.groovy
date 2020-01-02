package reactor


import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType


class Reactor8098000 {
   ReactorActionManager rm

   def act() {
      int map = rm.getPlayer().getMapId()
      int b = Math.abs(rm.getPlayer().getMapId() - 809050005)
      if (map != 809050000 && map != 809050010 && map != 809050014) {
         rm.spawnMonster(9400217 - b, 2)
         rm.spawnMonster(9400218 - b, 3)
      } else {
         rm.spawnMonster(9400209, 6)
         rm.spawnMonster(9400210, 9)
      }
      MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, "Some monsters are summoned.")
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor8098000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor8098000(rm: rm))
   return (Reactor8098000) getBinding().getVariable("reactor")
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