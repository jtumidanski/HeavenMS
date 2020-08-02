package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest22002 {
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
         qm.sendNext(I18nMessage.from("22002_DID_YOU_FEED"))
      } else if (status == 1) {
         qm.sendAcceptDecline(I18nMessage.from("22002_I_WILL_GIVE_YOU_THIS"))
      } else if (status == 2) {
         if (mode == 0) {//decline
            qm.sendNext(I18nMessage.from("22002_BREAKFAST_IS_IMPORTANT"))
            qm.dispose()
         } else {
            qm.gainItem(2022620, true)
            qm.forceStartQuest()
            qm.sendNext(I18nMessage.from("22002_HEAD_BACK_INSIDE"))
         }
      } else if (status == 3) {
         qm.showInfo("UI/tutorial/evan/3/0")
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
         qm.sendNext(I18nMessage.from("22002_DID_YOU_EAT"))
      } else if (status == 1) {
         qm.forceCompleteQuest()
         qm.gainItem(1003028, (short) 1, true)
         qm.gainItem(2022621, (short) 5, true)
         qm.gainItem(2022622, (short) 5, true)
         qm.gainExp(60)
         qm.showInfo("UI/tutorial/evan/4/0")
         qm.dispose()
      }
   }
}

Quest22002 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest22002(qm: qm))
   }
   return (Quest22002) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}