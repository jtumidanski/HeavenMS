package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2023000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int[] toMap = [211040200, 220050300, 220000000, 240030000]
   int[] inMap = [211000000, 220000000, 221000000, 240000000]
   int[] cost = [10000, 25000, 25000, 65000]
   int location

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.sendNext(I18nMessage.from("2023000_NOT_CHEAP"))
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            for (int i = 0; i < toMap.length; i++) {
               if (inMap[i] == cm.getPlayer().getMap().getId()) {
                  location = i
                  break
               }
            }
            cm.sendNext(I18nMessage.from("2023000_HELLO").with(inMap[location], toMap[location], cost[location]))
         } else if (status == 1) {
            cm.sendYesNo(I18nMessage.from("2023000_WOULD_YOU_LIKE_TO").with(cost[location], + toMap[location]))
         } else if (status == 2) {
            if (cm.getMeso() < cost[location]) {
               cm.sendNext(I18nMessage.from("2023000_NOT_ENOUGH_MESOS"))
            } else {
               cm.warp(toMap[location], location != 1 ? 0 : 1)
               cm.gainMeso(-cost[location])
            }
            cm.dispose()
         }
      }
   }
}

NPC2023000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2023000(cm: cm))
   }
   return (NPC2023000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }