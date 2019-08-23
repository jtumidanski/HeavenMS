package quest

import scripting.event.EventManager
import scripting.quest.QuestActionManager

class Quest21613 {
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
            qm.sendNext("We're a pack of wolves looking for our lost child. I hear you are taking care of our baby. We appreciate your kindness, but it's time to return our baby to us.", (byte) 9)
         } else if (status == 1) {
            qm.sendNextPrev("Werewolf is my friend, I can't just hand over a friend.", (byte) 3)
         } else if (status == 2) {
            qm.sendAcceptDecline("We understand, but we won't leave without our pup. Tell you what, we'll test you to see if you are worthy of raising a wolf. #rGet ready to be tested by wolves.#k")
         } else if (status == 3) {
            EventManager em = qm.getEventManager("Aran_3rdmount")
            if (em == null) {
               qm.sendOk("Sorry, but the 3rd mount quest (Wolves) is closed.")
               qm.dispose()
            } else {
               if (!em.startInstance(qm.getPlayer())) {
                  qm.sendOk("There is currently someone in this map, come back later.")
               } else {
                  qm.forceStartQuest()
               }

               qm.dispose()
            }
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21613 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21613(qm: qm))
   }
   return (Quest21613) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}