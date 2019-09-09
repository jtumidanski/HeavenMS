package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1052006 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int zones = 0
   int cost = 1000

   def start() {
      cm.sendNext("Hi, I'm the ticket salesman.")
      if (cm.isQuestStarted(2055) || cm.isQuestCompleted(2055)) {
         zones++
      }
      if (cm.isQuestStarted(2056) || cm.isQuestCompleted(2056)) {
         zones++
      }
      if (cm.isQuestStarted(2057) || cm.isQuestCompleted(2057)) {
         zones++
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         cm.dispose()
         return
      }
      if (status == 0) {
         if (zones == 0) {
            cm.dispose()
         } else {
            String selStr = "Which ticket would you like?#b"
            for (def i = 0; i < zones; i++ )
            selStr += "\r\n#L" + i + "#Construction site B" + (i + 1) + " (" + cost + " mesos)#l"
            cm.sendSimple(selStr)
         }
      } else if (status == 1) {
         if (cm.getMeso() < cost) {
            cm.sendOk("You do not have enough mesos.")
         } else {
            cm.gainMeso(-cost)
            if (selection < 0 || selection > zones) {
               cm.getClient().disconnect(false, false)
               return
            }
            cm.gainItem(4031036 + selection, (short) 1)
         }
         cm.dispose()
      }
   }
}

NPC1052006 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052006(cm: cm))
   }
   return (NPC1052006) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }