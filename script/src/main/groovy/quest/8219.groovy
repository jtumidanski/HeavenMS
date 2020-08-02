package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest8219 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk(I18nMessage.from("8219_OKAY_THEN"))
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("8219_TIME_IS_NOW"))
      } else if (status == 1) {
         qm.sendOk(I18nMessage.from("8219_CURRENTLY_WANDERING"))
         qm.forceStartQuest()
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
            qm.sendOk(I18nMessage.from("8219_SEE_YOU_AROUND"))
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("8219_WHO_ARE_YOU"))
      } else if (status == 1) {
         qm.sendOk(I18nMessage.from("8219_APPRAISE_YOU_NICELY"))
      } else if (status == 2) {
         if (qm.canHold(3992040, 1)) {
            qm.forceCompleteQuest()
            qm.gainItem(3992040, (short) 1)
            qm.gainExp(175000)
            qm.dispose()
         } else {
            qm.sendOk(I18nMessage.from("8219_NEED_SETUP_SLOT"))
         }
      } else if (status == 3) {
         qm.dispose()
      }
   }
}

Quest8219 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest8219(qm: qm))
   }
   return (Quest8219) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}