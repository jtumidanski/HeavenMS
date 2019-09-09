package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1061006 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int zones = 0
   String[] names = ["Deep Forest of Patience 1", "Deep Forest of Patience 2", "Deep Forest of Patience 3"]
   int[] maps = [105040310, 105040312, 105040314]
   int selectedMap = -1

   def start() {
      cm.sendNext("You feel a mysterious force surrounding this statue.")
      if (cm.isQuestStarted(2054) || cm.isQuestCompleted(2054)) {
         zones = 3
      } else if (cm.isQuestStarted(2053) || cm.isQuestCompleted(2053)) {
         zones = 2
      } else if (cm.isQuestStarted(2052) || cm.isQuestCompleted(2052)) {
         zones = 1
      } else {
         zones = 0
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (status >= 2 && mode == 0) {
            cm.sendOk("Alright, see you next time.")
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 1) {
            if (zones == 0) {
               cm.dispose()
            } else {
               String selStr = "Its power allows you to will yourself deep inside the forest.#b"
               for (def i = 0; i < zones; i++) {
                  selStr += "\r\n#L" + i + "#" + names[i] + "#l"
               }
               cm.sendSimple(selStr)
            }
         } else if (status == 2) {
            cm.warp(maps[selection], 0)
            cm.dispose()
         }
      }
   }
}

NPC1061006 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1061006(cm: cm))
   }
   return (NPC1061006) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }