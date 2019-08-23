package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2071012 {
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
            if (cm.getQuestProgress(23647, 0) != 0) {
               cm.dispose()
               return
            }

            if (!cm.haveItem(4031793, 1)) {
               cm.sendOk("Umm... Hey... Would you help me find a #bsoft and shiny silver fur#k that I lost on the woods? I need it, I need it, I need it sooooo much!")
               cm.dispose()
               return
            }

            cm.sendYesNo("Hey... Umm... Would you help me find a #bsoft and shiny silver fur#k that I lost on the woods? I need it, I need it, I need it sooooo much! ... Oh you found it!!! Will you give it to me?")
         } else if (status == 1) {
            cm.sendNext("Teehehee~ That's your reward for taking it from me, serves you well.")
            cm.gainItem(4031793, (short) -1)
            cm.gainFame(-5)
            cm.setQuestProgress(23647, 0, 1)

            cm.dispose()
         }
      }
   }
}

NPC2071012 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2071012(cm: cm))
   }
   return (NPC2071012) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }