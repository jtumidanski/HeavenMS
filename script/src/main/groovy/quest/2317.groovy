package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2317 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk(I18nMessage.from("2317_BREAKING_THE_BARRIER"))
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("2317_I_REMEMBER"))
      } else if (status == 1) {
         qm.forceStartQuest()
         qm.sendOk(I18nMessage.from("2317_PLEASE_DEFEAT"))
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
         qm.sendOk(I18nMessage.from("2317_HAVE_YOU"))
      } else if (status == 1) {
         qm.forceCompleteQuest()
         qm.gainExp(13500)
         qm.gainItem(4000500, (short) -100)
         qm.sendOk(I18nMessage.from("2317_I_AM_AMAZED"))
      } else if (status == 2) {
         qm.dispose()
      }
   }
}

Quest2317 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2317(qm: qm))
   }
   return (Quest2317) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}