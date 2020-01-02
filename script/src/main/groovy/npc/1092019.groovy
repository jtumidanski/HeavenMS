package npc

import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1092019 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int seagullProgress
   int seagullIdx = -1
   String[] seagullQuestion = ["One day, I went to the ocean and caught 62 Octopi for dinner. But then some kid came by and gave me 10 Octopi as a gift! How many Octopi do I have then, in total?"]
   String[] seagullAnswer = ["72"]

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
            if (!cm.isQuestStarted(6400)) {
               cm.sendOk("Who are you talking to me? If you're just bored, go bother somebody else.")
               cm.dispose()
            } else {
               seagullProgress = cm.getQuestProgressInt(6400, 1)

               if (seagullProgress == 0) {
                  seagullIdx = Math.floor(Math.random() * seagullQuestion.length).intValue()
                  cm.sendNext("Ok then! I'll give you the first question now! You better be ready because this one's a hard one. Even the seagulls here think this one's pretty tough. It's a pretty difficult problem.")
               } else if (seagullProgress == 1) {
                  cm.sendNext("Now~ Let's go onto the next question. This one is really difficult. I am going to have Bart help me on this one. You know Bart, right?")
               } else {
                  cm.sendNext("Ohhhh! Now that was impressive! I considered my test quite difficult, and for you to pass that... you are indeed an integral member of the Pirate family, and a friend of seagulls. We are now bonded by the mutual friendship that will last a lifetime! And, most of all, friends are there to help you out when you are in dire straits. If you are in a state of emergency, call us seagulls.")
               }
            }
         } else if (status == 1) {
            if (seagullProgress == 0) {
               cm.sendGetText(seagullQuestion[seagullIdx])
            } else if (seagullProgress == 1) {
               cm.sendNextPrev("I'm going to send you to an empty room in The Nautilus. You will see 9 Barts there. Hahaha~ Are they twins? No, no, certainly not. I've used a bit of magic for this test of will.")
            } else {
               cm.sendNextPrev("Notify us using the skill Air Strike, and we will be there to help you out, because that's what friends are for.\r\n\r\n  #s5221003#    #b#q5221003##k")
            }
         } else if (status == 2) {
            if (seagullIdx > -1) {
               String answer = cm.getText()
               if (answer == seagullAnswer[seagullIdx]) {
                  cm.sendNext("What! I can't believe how incredibly smart you are! Incredible! In the seagull world, that kind of intelligence would give you a Ph.D. and then some. You're really amazing... I can't believe it... I simply can't believe it!")
                  cm.setQuestProgress(6400, 1, 1)
                  cm.dispose()
               } else {
                  cm.sendOk("Hmm, that's not quite how I recall it. Try again!")
                  cm.dispose()
               }
            } else if (seagullProgress != 2) {
               cm.sendNextPrev("Anyway, only one of 9 Barts is the real Bart. You know that Pirates are known for the strength of their friendships and camaraderie with their fellow pirates. If you're a true pirate, you should be able to find your own mate with ease. Alright then, I'll send you to the room where Bart is.")
            } else {
               cm.sendNextPrev("You have met all my challenges, and passed! Good job!")
               cm.dispose()
            }
         } else if (status == 3) {
            EventManager em = cm.getEventManager("4jaerial")
            if (!em.startInstance(cm.getPlayer())) {
               cm.sendOk("Another player is already challenging the test in this channel. Please try another channel, or wait for the current player to finish.")
            }

            cm.dispose()
         }
      }
   }
}

NPC1092019 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1092019(cm: cm))
   }
   return (NPC1092019) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }