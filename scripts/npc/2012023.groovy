package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Maple Marble
	Map(s): 		4th job skill - Night Lord
	Description: 	
*/


class NPC2012023 {
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
            if (cm.haveItem(4031476)) {
               cm.gainItem(4031476, (short) -1)

               if (cm.canHold(4031456, 1)) {
                  cm.gainItem(4031456, (short) 1)
               }
            }

            cm.dispose()
         }
      }
   }
}

NPC2012023 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2012023(cm: cm))
   }
   return (NPC2012023) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }