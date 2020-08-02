package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2316 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk(I18nMessage.from("2316_WHY"))
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("2316_I_HAVE_HEARD_OF_A_POTION"))
      } else if (status == 1) {
         qm.forceStartQuest()
         qm.sendOk(I18nMessage.from("2316_I_AM_CONFIDENT"))
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
         qm.sendOk(I18nMessage.from("2316_NEED_SOME_SPORES"))
      } else if (status == 1) {
         qm.forceCompleteQuest()
         qm.gainExp(4200)
         qm.sendOk(I18nMessage.from("2316_I_HAVE_HEARD_OF_THEM"))
      } else if (status == 2) {
         qm.dispose()
      }
   }
}

Quest2316 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2316(qm: qm))
   }
   return (Quest2316) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}