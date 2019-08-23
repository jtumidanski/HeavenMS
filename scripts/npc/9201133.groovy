package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201133 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int map = 677000010
   int quest = 28283
   boolean inHuntingGround

   def start() {
      inHuntingGround = (cm.getMapId() >= 677000010 && cm.getMapId() <= 677000012)
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         status++
      } else {
         cm.dispose()
         return
      }
      if (status == 0) {
         if (!inHuntingGround) {
            if (cm.isQuestStarted(quest)) {
               if (!cm.getPlayer().haveItemEquipped(1003036)) {
                  cm.sendOk("The path ahead has a weird stench... Equip the #rgas mask#k before entering.")
                  cm.dispose()
                  return
               }

               cm.sendYesNo("Would you like to move to #b#m" + map + "##k?")
            } else {
               cm.sendOk("The entrance is blocked by a strange force.")
               cm.dispose()
            }
         } else {
            if (cm.getMapId() == 677000011) {
               map = 677000012
               cm.sendYesNo("Would you like to move to #b#m" + map + "##k?")
            } else {
               map = 105050400
               cm.sendYesNo("Would you like to #bexit this place#k?")
            }
         }
      } else {
         cm.warp(map, 0)
         cm.dispose()
      }
   }
}

NPC9201133 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201133(cm: cm))
   }
   return (NPC9201133) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }