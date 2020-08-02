package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest22007 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && type == 0) {
         status--
      } else if (mode == -1) {
         qm.dispose()
         return
      } else {
         status++
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("22007_DID_YOU_BRING"))
      } else if (status == 1) {
         qm.sendYesNo(I18nMessage.from("22007_HERE_YOU_GO"))
      } else if (status == 2) {
         if (mode == 0) {//decline
            qm.sendNext(I18nMessage.from("22007_THAT_IS_STRANGE"))
         } else {
            qm.gainItem(4032451, (short) -1)
            qm.forceCompleteQuest()
            qm.gainExp(360)
            qm.showInfo("UI/tutorial/evan/9/0")
         }
      } else if (status == 3) {
         qm.dispose()
      }
   }
}

Quest22007 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest22007(qm: qm))
   }
   return (Quest22007) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}