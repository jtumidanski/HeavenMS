package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest3321 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            qm.sendNext(I18nMessage.from("3321_HELLO"))
         } else if (status == 1) {
            qm.sendNextPrev(I18nMessage.from("3321_MY_CREATION"))
         } else if (status == 2) {
            qm.sendAcceptDecline(I18nMessage.from("3321_I_MUST_NOT_BE_STOPPED"))
         } else if (status == 3) {
            qm.sendNext(I18nMessage.from("3321_MY_GRATITUDE"))
         } else if (status == 4) {
            qm.sendNext(I18nMessage.from("3321_PERSONAL_FAVOR"))
         } else if (status == 5) {
            qm.sendNext(I18nMessage.from("3321_REMEMBER"))
            qm.forceStartQuest()
         } else if (status == 6) {
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest3321 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3321(qm: qm))
   }
   return (Quest3321) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}