package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest20002 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode > 0) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            qm.sendNext(I18nMessage.from("20002_NEINHEART_SENT_YOU_HERE"))
         } else if (status == 1) {
            qm.sendNext(I18nMessage.from("20002_RACE_CALLED_PIYOS"))
         } else if (status == 2) {
            qm.sendAcceptDecline(I18nMessage.from("20002_NO_MONSTERS_IN_EREVE"))
         } else if (status == 3) {
            qm.forceStartQuest()
            qm.forceCompleteQuest()
            qm.gainExp(60)
            qm.gainItem(2000020, (short) 10) // Red Potion for Noblesse * 10
            qm.gainItem(2000021, (short) 10) // Blue Potion for Noblesse * 10
            qm.gainItem(1002869, (short) 1)  // Elegant Noblesse Hat * 1
            qm.sendOk(I18nMessage.from("20002_PREPARE_YOURSELF"))
         } else if (status == 4) {
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20002 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20002(qm: qm))
   }
   return (Quest20002) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}