package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest8227 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk(I18nMessage.from("8227_NEEDS_YOU_COOPERATING"))
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("8227_HEY_BUDDY"))
      } else if (status == 1) {
         if (qm.canHold(4032032, 1)) {
            qm.gainItem(4032032, (short) 1)
            qm.sendOk(I18nMessage.from("8227_COUNTING_ON_YOU"))
            qm.forceStartQuest()
         } else {
            qm.sendOk(I18nMessage.from("8227_NEED_ETC_SPACE"))
         }
      } else if (status == 2) {
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
         if (qm.haveItem(4032032, 1)) {
            qm.gainItem(4032032, (short) -1)
            qm.sendOk(I18nMessage.from("8227_YOU_BROUGHT_THE_LETTER"))
            qm.forceCompleteQuest()
         } else {
            qm.sendOk(I18nMessage.from("8227_COME_ON"))
         }
      } else if (status == 1) {
         qm.dispose()
      }
   }
}

Quest8227 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest8227(qm: qm))
   }
   return (Quest8227) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}