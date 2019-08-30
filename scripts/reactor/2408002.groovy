package reactor

import client.MapleCharacter
import client.inventory.Item
import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager
import server.maps.MapleMap
import server.maps.MapleReactor
import tools.MessageBroadcaster
import tools.ServerNoticeType


class Reactor2408002 {
   ReactorActionManager rm

   def act() {
      EventInstanceManager eim = rm.getPlayer().getEventInstance()
      MapleMap womanfred = eim.getMapFactory().getMap(240050100)
      int vvpMap = rm.getPlayer().getMapId()
      int vvpKey
      int vvpOrig = 4001088
      int vvpStage = -1
      eim.showClearEffect(false, vvpMap)
      MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.LIGHT_BLUE, "The key has been teleported somewhere...")
      switch (vvpMap) {
         case 240050101:
            vvpKey = vvpOrig
            vvpStage = 1
            break
         case 240050102:
            vvpKey = vvpOrig + 1
            vvpStage = 2
            break
         case 240050103:
            vvpKey = vvpOrig + 2
            vvpStage = 3
            break
         case 240050104:
            vvpKey = vvpOrig + 3
            vvpStage = 4
            break
         default:
            vvpKey = -1
            break
      }

      eim.setIntProperty(vvpStage + "stageclear", 1)

      Item tehWomanfred = new Item(vvpKey, (short) 0, (short) 1)
      MapleReactor theWomanfred = womanfred.getReactorByName("keyDrop1")
      MapleCharacter dropper = eim.getPlayers().get(0)
      womanfred.spawnItemDrop(theWomanfred, dropper, tehWomanfred, theWomanfred.getPosition(), true, true)
      MessageBroadcaster.getInstance().sendMapServerNotice(eim.getMapInstance(240050100), ServerNoticeType.LIGHT_BLUE, "A bright flash of light, then a key suddenly appears somewhere in the map.")
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2408002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2408002(rm: rm))
   return (Reactor2408002) getBinding().getVariable("reactor")
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

def untouch() {
   getReactor().untouch()
}