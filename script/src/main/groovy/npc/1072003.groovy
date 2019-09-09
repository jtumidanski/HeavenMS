package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1072003 {
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
            if (cm.isQuestCompleted(100010)) {
               cm.sendOk("You're truly a hero!")
               cm.dispose()
            } else if (cm.isQuestCompleted(100009)) {
               cm.sendNext("Alright I'll let you in! Defeat the monsters inside, collect 30 Dark Marbles, then strike up a conversation with a colleague of mine inside. He'll give you #bThe Proof of a Hero#k, the proof that you've passed the test. Best of luck to you.")
               status = 3
            } else if (cm.isQuestStarted(100009)) {
               cm.sendNext("Oh, isn't this a letter from the #bDark Lord#k?")
            } else {
               cm.sendOk("I can show you the way once your ready for it.")
               cm.dispose()
            }
         } else if (status == 1) {
            cm.sendNextPrev("So you want to prove your skills? Very well...")
         } else if (status == 2) {
            cm.sendAcceptDecline("I will give you a chance if you're ready.")
         } else if (status == 3) {
            cm.sendOk("You will have to collect me #b30 #t4031013##k. Good luck.")
            cm.completeQuest(100009)
            cm.startQuest(100010)
            cm.gainItem(4031011, (short) -1)
         } else if (status == 4) {
            cm.warp(108000400, 0)
            cm.dispose()
         } else {
            cm.dispose()
         }
      }
   }
}

NPC1072003 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1072003(cm: cm))
   }
   return (NPC1072003) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }