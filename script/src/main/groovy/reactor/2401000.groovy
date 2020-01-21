package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.*

class Reactor2401000 extends SimpleReactor {
   def act() {
      rm.changeMusic("Bgm14/HonTale")
      if (rm.getReactor().getMap().getMonsterById(8810026) == null) {
         rm.getReactor().getMap().spawnHorntailOnGroundBelow(new Point(71, 260))

         EventInstanceManager eim = rm.getEventInstance()
         eim.restartEventTimer(60 * 60000)
      }
      MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("HORN_TAIL_SUMMONED"))
   }
}

Reactor2401000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2401000(rm: rm))
   return (Reactor2401000) getBinding().getVariable("reactor")
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