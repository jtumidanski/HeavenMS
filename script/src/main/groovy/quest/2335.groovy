package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2335 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || (mode == 0 && status == 0)) {
         qm.dispose()
         return
      } else if (mode == 0) {
         status--
      } else {
         status++
      }


      if (status == 0) {
         qm.sendNext(I18nMessage.from("2335_NOT_THE_END"))
      } else if (status == 1) {
         qm.sendAcceptDecline(I18nMessage.from("2335_FROM_WHAT_I_HAVE_HEARD"))
      } else if (status == 2) {
         if (qm.canHold(4032405)) {
            qm.gainItem(4032405, (short) 1)
            qm.sendNext(I18nMessage.from("2335_GOOD_LUCK"))
         } else {
            qm.sendOk(I18nMessage.from("2335_ETC_SPACE_NEEDED"))
            qm.dispose()
         }
      } else if (status == 3) {
         qm.forceStartQuest()
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest2335 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2335(qm: qm))
   }
   return (Quest2335) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}