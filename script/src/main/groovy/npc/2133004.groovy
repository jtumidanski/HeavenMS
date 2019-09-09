package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2133004 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
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
            if (!cm.haveItem(4001163) || !cm.isEventLeader()) {
               cm.sendYesNo("Let your party leader show me the Purple Stone of Magic from here.\r\n\r\nOr maybe you want to #rleave this forest#k? Leaving now means to abandon your partners here, take that in mind.")
            } else {
               cm.sendNext("Great, you have the Purple Stone of Magic. I shall show you guys #bthe path leading to the Stone Altar#k. Come this way.")
            }
         } else if (status == 1) {
            if (!cm.haveItem(4001163)) {
               cm.warp(930000800)
            } else {
               cm.getEventInstance().warpEventTeam(930000600)
            }

            cm.dispose()
         }
      }
   }
}

NPC2133004 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2133004(cm: cm))
   }
   return (NPC2133004) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }