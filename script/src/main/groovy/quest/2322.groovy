package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2322 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendNext(I18nMessage.from("2322_COME_SEE_ME"))
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendYesNo(I18nMessage.from("2322_JUST_BREAKING_THE_BARRIER"))
      } else if (status == 1) {
         qm.sendNext(I18nMessage.from("2322_GOOD_LUCK"))
      } else if (status == 2) {
         //qm.forceStartQuest();
         //qm.forceStartQuest(2322, "1");
         qm.gainExp(11000)
         qm.sendOk(I18nMessage.from("2322_GOOD_JOB"))
         qm.forceCompleteQuest()
      } else if (status == 3) {
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
         qm.sendOk(I18nMessage.from("2322_SHUT_OFF_ENTRANCE"))
      } else if (status == 1) {
         qm.forceCompleteQuest()
         qm.gainExp(11000)
         qm.sendOk(I18nMessage.from("2322_GOOD_JOB"))
      } else if (status == 2) {
         qm.dispose()
      }
   }
}

Quest2322 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2322(qm: qm))
   }
   return (Quest2322) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}