package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest3353 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
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
            qm.sendNext(I18nMessage.from("3353_THAT_IS_WHY"))
         } else if (status == 1) {
            qm.sendAcceptDecline(I18nMessage.from("3353_GO_THERE_AGAIN"))
         } else if (status == 2) {
            qm.warp(926120200, 1)

            qm.forceStartQuest()
            qm.dispose()
         }
      }
   }
}

Quest3353 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3353(qm: qm))
   }
   return (Quest3353) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}