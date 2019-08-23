package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9105005 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int area

   def start() {
      area = cm.getMapId() % 10
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
            if (area > 0) {
               cm.sendYesNo("Do you wish to leave this place?")
            } else {
               cm.sendYesNo("Do you wish to return to #bHappyville#k?")
            }
         } else {
            if (area > 0) {
               cm.warp(cm.getMapId() + 1, 0)
            } else {
               cm.warp(209000000)
            }

            cm.dispose()
         }
      }
   }
}

NPC9105005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9105005(cm: cm))
   }
   return (NPC9105005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }