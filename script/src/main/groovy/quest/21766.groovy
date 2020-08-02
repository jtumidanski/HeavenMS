package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21766 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (status == 0) {
         qm.sendNext(I18nMessage.from("21766_DO_ME_A_FAVOR"))
      } else if (status == 1) {
         qm.sendNext(I18nMessage.from("21766_USED_TO_SCOWL"))
      } else if (status == 2) {
         qm.sendNext(I18nMessage.from("21766_SECRET_BEHIND_THAT_WOODEN_BOX"))
      } else if (status == 3) {
         qm.sendNext(I18nMessage.from("21766_YOU_KNOW_WHERE"))
      } else if (status == 4) {
         qm.forceStartQuest()
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.forceCompleteQuest()
      qm.gainExp(200)
      qm.dispose()
   }
}

Quest21766 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21766(qm: qm))
   }
   return (Quest21766) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}