package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest22500 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && type == 0) {
         status--
      } else if (mode == -1) {
         qm.dispose()
         return
      } else {
         status++
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("22500_FINALLY_HERE"))
      } else if (status == 1) {
         qm.sendNextPrev("#bWhoooooa, it talks!", (byte) 2)
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("22500_MY_MASTER_IS_STRANGE"))
      } else if (status == 3) {
         qm.sendNextPrev("#bEh? What do you mean? We'll be seeing a lot of each other? What pact?", (byte) 2)
      } else if (status == 4) {
         qm.sendNextPrev(I18nMessage.from("22500_YOU_WOKE_ME"))
      } else if (status == 5) {
         qm.sendNextPrev("#bWhaaat? A Dragon? You're a Dragon?! I don't get it... Why am I your master? What are you talking about?", (byte) 2)
      } else if (status == 6) {
         qm.sendNextPrev(I18nMessage.from("22500_WHAT_ARE_YOU_TALKING_ABOUT"))
      } else if (status == 7) {
         qm.sendNextPrev("#bWait, wait, wait. Let me get this straight. You're saying I have no choice but to help you?", (byte) 2)
      } else if (status == 8) {
         qm.sendNextPrev(I18nMessage.from("22500_YOU_DO_NOT_WANT_TO_BE"))
      } else if (status == 9) {
         qm.sendNextPrev("#bNo... It's not that... I just don't know if I'm ready for a pet.", (byte) 2)
      } else if (status == 10) {
         qm.sendNextPrev(I18nMessage.from("22500_A_PET"))
      } else if (status == 11) {
         qm.sendNextPrev("#b...#b(You stare at him skeptically. He looks like a lizard. A puny little one, at that.)#k", (byte) 2)
      } else if (status == 12) {
         qm.sendAcceptDecline(I18nMessage.from("22500_SEE_WHAT_I_CAN_DO"))
      } else if (status == 13) {
         if (mode == 0 && type == 15) {
            qm.sendNext(I18nMessage.from("22500_DO_NOT_BELIEVE_ME"))
            qm.dispose()
         } else {
            if (!qm.isQuestStarted(22500)) {
               qm.forceStartQuest()
            }
            qm.sendNext(I18nMessage.from("22500_COMMAND_ME_TO"))
         }
      } else if (status == 14) {
         qm.sendNextPrev(I18nMessage.from("22500_DISTRIBUTE_AP"))
      } else if (status == 15) {
         qm.showInfo("UI/tutorial/evan/11/0")
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest22500 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest22500(qm: qm))
   }
   return (Quest22500) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}