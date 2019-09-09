package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2010008 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendSimple("What would you like to do?\r\n#b#L0#Create/Change your Guild Emblem#l#k")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            sel = selection
            if (sel == 0) {
               if (cm.getPlayer().getGuildRank() == 1) {
                  cm.sendYesNo("Creating or changing Guild Emblem costs #b 5000000 mesos#k, are you sure you want to continue?")
               } else {
                  cm.sendOk("You must be the Guild Leader to change the Emblem. Please tell your leader to speak with me.")
               }
            }
         } else if (status == 2 && sel == 0) {
            cm.getPlayer().genericGuildMessage(17)
            cm.dispose()
         } else {
            cm.dispose()
         }
      }
   }
}

NPC2010008 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2010008(cm: cm))
   }
   return (NPC2010008) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }