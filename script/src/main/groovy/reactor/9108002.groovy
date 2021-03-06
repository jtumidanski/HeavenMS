package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager
import server.maps.MapleMap
import server.maps.MapleReactor
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Reactor9108002 extends SimpleReactor {
   def act() {
      EventInstanceManager eim = rm.getEventInstance()
      if (eim != null) {
         MapleReactor react = rm.getReactor().getMap().getReactorByName("fullmoon")
         int stage = (eim.getProperty("stage")).toInteger() + 1
         String newStage = stage.toString()
         eim.setProperty("stage", newStage)
         react.forceHitReactor((byte) (react.getState() + 1))
         if (eim.getProperty("stage") == "6") {
            MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("PROTECT_THE_MOON_BUNNY"))
            MapleMap map = eim.getMapInstance(rm.getReactor().getMap().getId())
            map.allowSummonState(true)
            map.spawnMonsterOnGroundBelow(9300061, -183, -433)
         }
      }
   }
}

Reactor9108002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9108002(rm: rm))
   return (Reactor9108002) getBinding().getVariable("reactor")
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