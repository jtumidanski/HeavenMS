package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import server.life.MapleLifeFactory
import server.maps.MapleMap
import server.maps.MapleMapObject
import tools.MessageBroadcaster
import tools.ServerNoticeType
import tools.I18nMessage

import java.awt.*

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2112000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def playersTooClose() {
      Point npcPosition = cm.getMap().getMapObject(cm.getNpcObjectId()).position()
      MapleMapObject[] characterList = cm.getMap().getPlayers()

      for (Iterator<MapleMapObject> iterator = characterList.iterator(); iterator.hasNext();) {
         MapleMapObject chr = iterator.next()

         Point characterPosition = chr.position()
         if (Math.sqrt(Math.pow((npcPosition.getX() - characterPosition.getX()), 2) + Math.pow((npcPosition.getY() - characterPosition.getY()), 2)) < 310) {
            return true
         }
      }

      return false
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         EventInstanceManager eim = cm.getEventInstance()

         if (cm.getMapId() == 926100203) {
            if (status == 0) {
               int state = eim.getIntProperty("yuleteTalked")

               if (state == -1) {
                  cm.sendOk(I18nMessage.from("2112000_HAVE_FUN"))

               } else if (playersTooClose()) {
                  cm.sendOk(I18nMessage.from("2112000_OH_HELLO_THERE"))

                  eim.setIntProperty("yuleteTalked", -1)
               } else if (eim.getIntProperty("npcShocked") == 0) {
                  cm.sendOk(I18nMessage.from("2112000_QUITE_SNEAKY"))

                  eim.setIntProperty("yuleteTalked", -1)
               } else {
                  cm.sendOk(I18nMessage.from("2112000_HOW_DID_YOU_GET_HERE"))

                  eim.setIntProperty("yuleteTalked", 1)
               }
            }

            cm.dispose()
         } else {
            if (status == 0) {
               if (eim.isEventCleared()) {
                  cm.sendOk(I18nMessage.from("2112000_I_HAVE_BEEN_BEATEN"))
               } else {
                  int state = eim.getIntProperty("yuletePassed")

                  if (state == -1) {
                     cm.sendOk(I18nMessage.from("2112000_PINNACLE_OF_STUDIES"))
                  } else if (state == 0) {
                     cm.sendOk(I18nMessage.from("2112000_SUCH_A_PAIN"))
                     MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, I18nMessage.from("YULETE_MEET_FRANK"))

                     MapleMap map = eim.getMapInstance(926100401)
                     MapleLifeFactory.getMonster(9300139).ifPresent({ monster ->
                        map.spawnMonsterOnGroundBelow(monster, new Point(250, 100))
                        eim.setIntProperty("statusStg7", 1)
                        eim.setIntProperty("yuletePassed", -1)
                     })
                  } else {
                     cm.sendOk(I18nMessage.from("2112000_SUCH_A_PAIN_LONG"))
                     MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.PINK_TEXT, I18nMessage.from("YULETE_MEET_FRANK_LONG"))

                     MapleMap map = eim.getMapInstance(926100401)
                     MapleLifeFactory.getMonster(9300140).ifPresent({ monster ->
                        map.spawnMonsterOnGroundBelow(monster, new Point(250, 100))
                        eim.setIntProperty("statusStg7", 2)
                        eim.setIntProperty("yuletePassed", -1)
                     })
                  }
               }
            }

            cm.dispose()
         }
      }
   }
}

NPC2112000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2112000(cm: cm))
   }
   return (NPC2112000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }