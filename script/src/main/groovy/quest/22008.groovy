package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest22008 {
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
         qm.sendAcceptDecline(I18nMessage.from("22008_IT_IS_STRANGE"))
      } else if (status == 1) {
         if (mode == 0) {//decline
            qm.sendNext(I18nMessage.from("22008_ARE_YOU_SCARED_OF"))
            qm.dispose()
         } else {
            qm.forceStartQuest()
            qm.sendNext(I18nMessage.from("22008_DEFEAT_THOSE_FOXES"))
         }
      } else if (status == 2) {
         qm.showInfo("UI/tutorial/evan/10/0")
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && type == 0) {
         status--
      } else if (mode == -1) {
         qm.dispose()
         return
      } else {
         status++
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("22008_DID_YOU_DEFEAT"))
      } else if (status == 1) {
         qm.sendNextPrev("#bWhat happened to slaying the Foxes left behind?", (byte) 2)
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("22008_I_DID_CHASE_THEM"))
      } else if (status == 3) {
         qm.sendNextPrev("#bAre you sure you weren't just hiding because you were scared of the Foxes?", (byte) 2)
      } else if (status == 4) {
         qm.sendNextPrev(I18nMessage.from("22008_NO_WAY"))
      } else if (status == 5) {
         qm.sendNextPrev("#bWatch out! There's a #o9300385# right behind you!", (byte) 2)
      } else if (status == 6) {
         qm.sendNextPrev(I18nMessage.from("22008_EEEK"))
      } else if (status == 7) {
         qm.sendNextPrev("#b...", (byte) 2)
      } else if (status == 8) {
         qm.sendNextPrev(I18nMessage.from("22008_...."))
      } else if (status == 9) {
         qm.sendNextPrev(I18nMessage.from("22008_YOU_LITTLE_BRAT"))
      } else if (status == 10) {
         qm.sendNextPrev("#b(This is why I don't want to call you Older Brother...)", (byte) 2)
      } else if (status == 11) {
         qm.sendNextPrev(I18nMessage.from("22008_ANYWAY"))
      } else if (status == 12) {
         if (!qm.isQuestCompleted(22008)) {
            qm.gainItem(1372043, true)
            qm.gainItem(2022621, (short) 25, true)
            qm.gainItem(2022622, (short) 25, true)
            qm.forceCompleteQuest()
            qm.gainExp(910)
         }
         qm.sendNextPrev(I18nMessage.from("22008_THIS_IS_A_WEAPON"))
      } else if (status == 13) {
         qm.sendPrev(I18nMessage.from("22008_HOW_WEIRD_IS_THAT"))
      } else if (status == 14) {
         qm.dispose()
      }
   }
}

Quest22008 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest22008(qm: qm))
   }
   return (Quest22008) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}