package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Pison
	Map(s): 		Florina Beach
	Description: 	
*/


class NPC1081001 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int returnMap

   def start() {
      returnMap = cm.getPlayer().peekSavedLocation("FLORINA")
      if (returnMap == -1) {
         returnMap = 104000000
      }
      cm.sendNext("So you want to leave #b#m110000000##k? If you want, I can take you back to #b#m" + returnMap + "##k.")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else if (mode == 0) {
         cm.sendNext("You must have some business to take care of here. It's not a bad idea to take some rest at #m" + returnMap + "# Look at me; I love it here so much that I wound up living here. Hahaha anyway, talk to me when you feel like going back.")
         cm.dispose()
      } else if (mode == 1) {
         status++
         if (status == 1) {
            cm.sendYesNo("Are you sure you want to return to #b#m" + returnMap + "##k? Alright, we'll have to get going fast. Do you want to head back to #m" + returnMap + "# now?")
         } else {
            cm.getPlayer().getSavedLocation("FLORINA")
            cm.warp(returnMap)
            cm.dispose()
         }
      }
   }
}

NPC1081001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1081001(cm: cm))
   }
   return (NPC1081001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }