package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2112016 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.isQuestStarted(3367)) {
         int c = cm.getQuestProgress(3367, 30)
         if (c == 30) {
            cm.sendNext("(All files have been organized. Report the found files to Yulete.)", (byte) 2)
            cm.dispose()
            return
         }

         int book = (cm.getNpcObjectId() % 30)
         int prog = cm.getQuestProgress(3367, book)
         if (prog == 0) {
            c++

            if (book < 20) {
               if (!cm.canHold(4031797, 1)) {
                  cm.sendNext("(You found a report file, but since your ETC is full you choose to put the file in the place you've found.)")
                  cm.dispose()
                  return
               } else {
                  cm.gainItem(4031797, (short) 1)
                  cm.setQuestProgress(3367, 31, cm.getQuestProgress(3367, 31) + 1)
               }
            }

            cm.sendNext("(Organized file. #r" + (30 - c) + "#k left.)", (byte) 2)

            cm.setQuestProgress(3367, book, 1)
            cm.setQuestProgress(3367, 30, c)
         }
      }

      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC2112016 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2112016(cm: cm))
   }
   return (NPC2112016) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }