package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1040000 {
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
         if (mode == 1)
            status++
         else
            status--

         if(status == 0) {
            if(cm.isQuestStarted(28177) && !cm.haveItem(4032479)) {
               if(cm.canHold(4032479)) {
                  cm.gainItem(4032479, (short) 1)
                  cm.sendOk("Huh, are you looking for me? Chief Stan sent you here, right? But hey, I am not the suspect you seek. If I have some proof? Here, take this and return it to #b#p1012003##k.")
               } else {
                  cm.sendOk("Hey, make a slot available before talking to me.")
               }
            } else {
               cm.sendOk("Zzzzzz...")
            }

            cm.dispose()
         }
      }
   }
}

NPC1040000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1040000(cm: cm))
   }
   return (NPC1040000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }