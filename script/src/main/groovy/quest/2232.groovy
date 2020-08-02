package quest
import tools.I18nMessage

import client.MapleFamilyEntry
import scripting.quest.QuestActionManager

class Quest2232 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      MapleFamilyEntry familyEntry = qm.getPlayer().getFamilyEntry()
      if (familyEntry != null && familyEntry.getJuniorCount() > 0) {
         qm.forceCompleteQuest()
         qm.gainExp(3000)
         qm.sendNext(I18nMessage.from("2232_GOOD_JOB"))
      } else {
         qm.sendNext(I18nMessage.from("2232_NOT_SUCCESSFUL"))
      }
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      MapleFamilyEntry familyEntry = qm.getPlayer().getFamilyEntry()
      if (familyEntry != null && familyEntry.getJuniorCount() > 0) {
         qm.forceCompleteQuest()
         qm.gainExp(3000)
         qm.sendNext(I18nMessage.from("2232_GOOD_JOB"))
      } else {
         qm.sendNext(I18nMessage.from("2232_NOT_SUCCESSFUL"))
      }
      qm.dispose()
   }
}

Quest2232 getQuest() {
   QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
   getBinding().setVariable("quest", new Quest2232(qm: qm))
   return (Quest2232) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}