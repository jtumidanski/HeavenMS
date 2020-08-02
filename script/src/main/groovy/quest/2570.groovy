package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2570 {
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
         qm.sendNext(I18nMessage.from("2570_GOOD_TO_SEE_YOU"))
      } else if (status == 1) {
         qm.sendNextPrev(I18nMessage.from("2570_CANNONEER_INFO"))
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("2570_HERO_BUSINESS_IS_PROFITABLE"))
      } else if (status == 3) {
         qm.sendNextPrev(I18nMessage.from("2570_PIRATE_JOB_INSTRUCTOR"))
      } else if (status == 4) {
         qm.sendNextPrev(I18nMessage.from("2570_I_HAVE_SAID_MY_PIECE"))
      } else if (status == 5) {
         qm.sendAcceptDecline(I18nMessage.from("2570_DO_YOU_WISH_TO_JOIN"))
      } else if (status == 6) {
         if (mode == 0 && qm.isQuestCompleted(2570)) {//decline
            qm.sendNext(I18nMessage.from("2570_I_UNDERSTAND"))
            qm.dispose()
         } else {
            if (!qm.isQuestCompleted(2570)) {
               qm.gainItem(1532000)
               qm.gainItem(1002610)
               qm.gainItem(1052095)
               qm.changeJobById(501)
               qm.forceCompleteQuest()
               qm.forceCompleteQuest(29900)
               qm.teachSkill(109, (byte) 1, (byte) 1, -1)
               qm.teachSkill(110, (byte) 0, (byte) -1, -1)//? blessing
               qm.teachSkill(111, (byte) 1, (byte) 1, -1)
               qm.showItemGain(1532000, 1002610, 1052095)
            }
            qm.sendNext(I18nMessage.from("2570_ONE_OF_US"))
         }
      } else if (status == 7) {
         qm.sendNextPrev(I18nMessage.from("2570_DISTRIBUTE_STATS"))
      } else if (status == 8) {
         qm.sendNextPrev(I18nMessage.from("2570_LITTLE_GIFT"))
      } else if (status == 9) {
         qm.sendNextPrev(I18nMessage.from("2570_ONE_LAST_THING"))
      } else if (status == 10) {
         qm.sendNextPrev(I18nMessage.from("2570_THAT_IS_IT"))
         qm.forceStartQuest(2945, 1)
      } else if (status == 11) {
         qm.dispose()//let them go back :P
      }
   }
}

Quest2570 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2570(qm: qm))
   }
   return (Quest2570) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}