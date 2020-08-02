package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest6030 {
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
            qm.sendNext(I18nMessage.from("6030_FUNDAMENTALS"))
         } else if (status == 1) {
            qm.sendNextPrev(I18nMessage.from("6030_GOOD_TO_TAKE_A_LOOK"))
         } else if (status == 2) {
            qm.sendNextPrev(I18nMessage.from("6030_IN_FACT"))
         } else if (status == 3) {
            qm.sendNextPrev(I18nMessage.from("6030_ALCHEMY_CAN_BE_EMPLOYED"))
         } else if (status == 4) {
            qm.sendNextPrev(I18nMessage.from("6030_AND_REMEMBER"))
         } else if (status == 5) {
            qm.gainMeso(-10000)

            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest6030 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest6030(qm: qm))
   }
   return (Quest6030) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}