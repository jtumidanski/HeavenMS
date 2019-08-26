package quest


import scripting.quest.QuestActionManager

class Quest2258 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            qm.sendAcceptDecline("Meerkats spreads rumors like wildfire... By blackmailing me and my cab service, they are taking costumers away from me day after day... Hey, tell no one about this, if you clean some #rMeerkats#k from my way, I'll tell you an info about the #rMushroom Castle#k. What do you say?")
         } else if (status == 1) {
            qm.sendNext("Great, they you have #r5 minutes#k to kill #b40 Meerkats#k within this time. Good luck!")
         } else if (status == 2) {
            qm.forceStartQuest()
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            qm.sendNext("You did it! ... Hey, #rMeerkats#k around here may listen to our conversation. I'm not going to talk about THAT right now.")
         } else if (status == 1) {
            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest2258 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2258(qm: qm))
   }
   return (Quest2258) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}