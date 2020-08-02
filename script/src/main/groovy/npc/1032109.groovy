package npc

import client.MapleCharacter
import scripting.npc.NPCConversationManager
import server.life.MapleLifeFactory
import server.maps.MapleMap
import tools.I18nMessage

import java.awt.*

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1032109 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int mobId = 2220100 //Blue Mushroom

   def start() {
      if (!cm.isQuestStarted(20718)) {
         cm.dispose()
         return
      }

      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || (mode == 0 && status == 0)) {
         cm.dispose()
         return
      } else if (mode == 0) {
         status--
      } else {
         status++
      }


      if (status == 0) {
         cm.sendOk(I18nMessage.from("1032109_A_LOT_OF_ANGRY_MONSTERS_SUMMONED"))
      } else if (status == 1) {
         MapleCharacter player = cm.getPlayer()
         MapleMap map = player.getMap()

         for (def i = 0; i < 10; i++) {
            MapleLifeFactory.getMonster(mobId).ifPresent({ monster -> map.spawnMonsterOnGroundBelow(monster, new Point(117, 183)) })
         }
         for (def i = 0; i < 10; i++) {
            MapleLifeFactory.getMonster(mobId).ifPresent({ monster -> map.spawnMonsterOnGroundBelow(monster, new Point(4, 183)) })
         }
         for (def i = 0; i < 10; i++) {
            MapleLifeFactory.getMonster(mobId).ifPresent({ monster -> map.spawnMonsterOnGroundBelow(monster, new Point(-109, 183)) })
         }

         cm.completeQuest(20718, 1103003)
         cm.gainExp(4000 * cm.getPlayer().getExpRate())
         cm.dispose()
      }
   }
}

NPC1032109 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1032109(cm: cm))
   }
   return (NPC1032109) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }