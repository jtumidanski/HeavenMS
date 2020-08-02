package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest20000 {
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
            qm.sendNext(I18nMessage.from("20000_THIS_IS_EXHILARATING"))
         } else if (status == 1) {
            qm.sendNext(I18nMessage.from("20000_BATTLE_AGAINST_EVIL"))
         } else if (status == 2) {
            qm.sendOk(I18nMessage.from("20000_I_AM_CONFIDENT"))
         } else if (status == 3) {
            qm.gainItem(1142065, (short) 1) // Noblesse Medal * 1
            qm.gainExp(20) //gain 20 exp!!
            qm.forceStartQuest()
            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20000 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20000(qm: qm))
   }
   return (Quest20000) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}