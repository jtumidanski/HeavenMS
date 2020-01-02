package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2112001 {
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
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            cm.sendSimple("Defeated... So, that's how Yulete's legacy will reach it's end, oh how woe is this... Hope you guys are happy now, as I will pass my days rotting in a dark cellar. Everything I've done was for the sake of Magatia!! (sob)\r\n #Ll# Hey man, come now, cheer up! There were not many damages that couldn't be resolved here. Magatia created these forbidding laws to protect it's people from the undoings a greater power like this would do if it reaches wrong hands. That's not the end for you, accept rehabilitation from the Societies and everything will work out!#l")
         } else if (status == 1) {
            cm.sendNext("... Are you guys forgiving me after all that I've done? Well, I guess I was blinded by the great source of power that could be discovered that way, maybe they're right saying a human can't simply fathom on the usage of those powers without corrupting themselves along the way... I am profoundly sorry, and to make myself up with everyone I'm willing to help the Societies again wherever I can on the progress of alchemy. Thank you.")
         } else {
            if (!cm.isQuestCompleted(7770)) {
               cm.completeQuest(7770)
            }

            cm.warp(926100600)
            cm.dispose()
         }
      }
   }
}

NPC2112001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2112001(cm: cm))
   }
   return (NPC2112001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }