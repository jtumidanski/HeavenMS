package npc

import scripting.npc.NPCConversationManager
import server.life.MapleLifeFactory

import java.awt.*

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1063017 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            cm.sendYesNo("Ahead awaits the Master himself. Are you ready to face him?")
         } else {
            if (cm.getClient().getChannelServer().getMapFactory().getMap(925020010).getCharacters().size() > 0) {
               cm.sendOk("Someone is already challenging the Master. Try again later.")
            } else {
               MapleLifeFactory.getMonster(9300346).ifPresent({ monster ->
                  cm.getWarpMap(910510202).spawnMonsterOnGroundBelow(monster, new Point(95, 200))
                  cm.warp(910510202, 0)
               })
            }

            cm.dispose()
         }
      }
   }
}

NPC1063017 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1063017(cm: cm))
   }
   return (NPC1063017) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }