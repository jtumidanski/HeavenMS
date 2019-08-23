package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Trash Can 3
	Map(s): 		
	Description: 	Kerning Subway
*/


class NPC1052111 {
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
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         } else if (mode == 0) {
            status--
         } else {
            status++
         }

         if (status == 0) {
            if (cm.isQuestStarted(20710)) {
               if (!cm.hasItem(4032136)) {
                  if (cm.canHold(4032136)) {
                     cm.gainItem(4032136, (short) 1)
                     cm.sendNext("You have found a #b#t4032136##k in the trash can!   #i4032136#")
                  } else {
                     cm.sendOk("#i4032136# Not enough space in your ETC inventory.")
                  }
               } else {
                  cm.sendOk("Just a trash can sitting there.")
               }
            } else {
               cm.sendOk("Just a trash can sitting there.")
            }
         } else if (status == 1) {
            cm.dispose()
         }
      }
   }
}

NPC1052111 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052111(cm: cm))
   }
   return (NPC1052111) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }