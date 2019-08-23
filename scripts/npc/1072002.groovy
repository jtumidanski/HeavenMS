package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Bowman Job Instructor
	Map(s): 		Warning Street : The Road to the Dungeon
	Description: 	Hunter Job Advancement
*/


class NPC1072002 {
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
            if (cm.isQuestCompleted(100001)) {
               cm.sendOk("You're truly a hero!")
               cm.dispose()
            } else if (cm.isQuestCompleted(100000)) {
               cm.sendNext("Alright I'll let you in! Defeat the monsters inside, collect 30 Dark Marbles, then strike up a conversation with a colleague of mine inside. He'll give you #bThe Proof of a Hero#k, the proof that you've passed the test. Best of luck to you.")
               status = 3
            } else if (cm.isQuestStarted(100000)) {
               cm.sendNext("Oh, isn't this a letter from #bAthena#k?")
            } else {
               cm.sendOk("I can show you the way once your ready for it.")
               cm.dispose()
            }
         } else if (status == 1) {
            cm.sendNextPrev("So you want to prove your skills? Very well...")
         } else if (status == 2) {
            cm.sendAcceptDecline("I will give you a chance if you're ready.")
         } else if (status == 3) {
            cm.completeQuest(100000)
            cm.startQuest(100001)
            cm.gainItem(4031010, (short) -1)
            cm.sendOk("You will have to collect me #b30 #t4031013##k. Good luck.")
         } else if (status == 4) {
            cm.warp(108000100)
            cm.dispose()
         } else {
            cm.dispose()
         }
      }
   }
}

NPC1072002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1072002(cm: cm))
   }
   return (NPC1072002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }