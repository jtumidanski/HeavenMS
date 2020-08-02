package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest20008 {
   QuestActionManager qm
   int status = -1
   int choice1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         qm.dispose()
      }
      else if (mode > 0)
         status++
      if (status == 0)
         qm.sendSimple(I18nMessage.from("20008_ARE_YOU_READY"))
      else if (status == 1) {
         if (selection == 0) {
            qm.sendNext(I18nMessage.from("20008_SHOW_EVERYONE_COURAGE"))
            qm.dispose()
         } else if (selection == 1) {
            choice1 = selection
            qm.sendSimple(I18nMessage.from("20008_GLAD_YOU_DID_NOT_RUN_AWAY"))
            qm.forceStartQuest()
            qm.forceCompleteQuest()
         }
      } else if (status == 2) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20008 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20008(qm: qm))
   }
   return (Quest20008) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}