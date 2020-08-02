package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest29901 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (qm.forceStartQuest()) {
         qm.showInfoText("You have earned the <Junior Adventurer> title. You can receive a Medal from NPC Dalair.")
      }
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         qm.dispose()
      } else {
         if (status == 0) {
            qm.sendNext(I18nMessage.from("29901_CONGRATULATIONS"))
         } else if (status == 1) {
            if (qm.canHold(1142108)) {
               qm.gainItem(1142108)
               qm.forceCompleteQuest()
               qm.dispose()
            } else {
               qm.sendNext(I18nMessage.from("29901_MAKE_ROOM"))
            }//NOT GMS LIKE
         } else if (status == 2) {
            qm.dispose()
         }
      }
   }
}

Quest29901 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest29901(qm: qm))
   }
   return (Quest29901) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}