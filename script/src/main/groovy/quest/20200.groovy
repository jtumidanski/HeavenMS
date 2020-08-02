package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest20200 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (status == 0 && mode == 0) {
            qm.sendNext(I18nMessage.from("20200_DO_YOU_FEEL"))
            qm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            qm.sendAcceptDecline(I18nMessage.from("20200_LEVEL_HAS_SKY_ROCKETED"))
         } else if (status == 1) {
            qm.startQuest()
            qm.completeQuest()
            qm.sendOk(I18nMessage.from("20200_KNIGHTHOOD_EXAM"))
         } else if (status == 2) {
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20200 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20200(qm: qm))
   }
   return (Quest20200) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}