package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9102100 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (status == 0 && mode == 0) {
         cm.sendNext("#b(I didn't touch this hidden item covered in grass)")
         cm.dispose()
         return
      }
      if (mode == 1) {
         status++
      } else {
         status--
      }
      if (status == 0) {
         if (cm.getQuestStatus(4646) == 1) {
            if (cm.haveItem(4031921)) {
               cm.sendNext("#b(What's this... eww... a pet's poop was in there!)")
               cm.dispose()
            } else {
               cm.sendYesNo("#b(I can see something covered in grass. Should I pull it out?)")
            }
         } else {
            cm.sendOk("#b(I couldn't find anything.)")
            cm.dispose()
         }
      } else if (status == 1) {
         cm.sendNext("I found the item that Pet Trainer Bartos hid... this note.")
         cm.gainItem(4031921, (short) 1)
         cm.dispose()
      }
   }
}

NPC9102100 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9102100(cm: cm))
   }
   return (NPC9102100) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }