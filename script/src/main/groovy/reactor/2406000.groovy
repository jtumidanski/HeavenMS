package reactor


import scripting.reactor.ReactorActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Reactor2406000 extends SimpleReactor {
   def act() {
      rm.spawnNpc(2081008)
      rm.startQuest(100203)
      MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("BABY_DRAGON_SUMMONED"))
   }

   def touch() {
      if (rm.haveItem(4001094) && rm.getReactor().getState() == ((byte) 0)) {
         rm.hitReactor()
         rm.gainItem(4001094, (short) -1)
      }
   }
}

Reactor2406000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2406000(rm: rm))
   return (Reactor2406000) getBinding().getVariable("reactor")
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