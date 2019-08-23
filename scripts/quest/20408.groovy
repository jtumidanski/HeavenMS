package quest


import scripting.quest.QuestActionManager

class Quest20408 {
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
            qm.sendNext("#h0#... First of all, thank you for your great work. If it weren't you, I... I wouldn't be safe from the curse of Black Witch. Thank you so much.")
         } else if (status == 1) {
            qm.sendNextPrev("If nothing else, this chain of events makes one thing crystal clear, you have put in countless hours of hard work to better yourself and contribute to the Cygnus Knights.")
         } else if (status == 2) {
            qm.sendAcceptDecline("To celebrate your hard work and accomplishments... I would like to award you a new title and renew my blessings onto you. Will you... accept this?")
         } else if (status == 3) {
            if (!qm.canHold(1142069, 1)) {
               qm.sendOk("Please, make a room available on your EQUIP inventory for the medal.")
               qm.dispose()
               return
            }

            qm.gainItem(1142069, (short) 1)
            if (qm.getJobId() % 10 == 1) {
               qm.changeJobById(qm.getJobId() + 1)
            }

            qm.forceStartQuest()
            qm.forceCompleteQuest()

            qm.sendOk("#h0#. For courageously battling the Black Mage, I will appoint you as the new Chief Knight of Cygnus Knights from this moment onwards. Please use your power and authority wisely to help protect the citizens of Maple World.")
         } else if (status == 4) {
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20408 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20408(qm: qm))
   }
   return (Quest20408) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}