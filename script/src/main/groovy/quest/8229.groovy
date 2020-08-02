package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest8229 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk(I18nMessage.from("8229_COME_ON"))
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("8229_I_KNEW_WE_COULD"))
      } else if (status == 1) {
         if (qm.haveItem(4032018, 1)) {
            qm.forceStartQuest()
         } else if (qm.canHold(4032018, 1)) {
            qm.gainItem(4032018, (short) 1)
            qm.forceStartQuest()
         } else {
            qm.sendOk(I18nMessage.from("8229_NEED_ETC_SPACE"))
         }

         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         if (qm.haveItem(4032018, 1)) {
            qm.sendOk(I18nMessage.from("8229_YOU_BROUGHT_IT"))
         } else {
            qm.sendOk(I18nMessage.from("8229_WHAT_IS_WRONG"))
            qm.dispose()
         }
      } else if (status == 1) {
         qm.gainItem(4032018, (short) -1)
         qm.gainExp(50000)
         qm.forceCompleteQuest()

         qm.dispose()
      }
   }
}

Quest8229 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest8229(qm: qm))
   }
   return (Quest8229) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}