package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest8230 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk(I18nMessage.from("8230_SEE_YOU_AROUND"))
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("8230_HEY_TRAVELER"))
      } else if (status == 1) {
         qm.sendOk(I18nMessage.from("8230_THAT_IS_THE_THING"))
         qm.forceStartQuest()
      } else if (status == 2) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++

      if (status == 0) {
         if (qm.haveItem(3992041)) {
            qm.sendOk(I18nMessage.from("8230_DID_YOU_ACCOMPLISH"))
            qm.forceCompleteQuest()
         } else if (qm.getQuestStatus(8223) == 2) {
            qm.sendOk(I18nMessage.from("8230_LOST_THE_KEY"))
         } else {
            qm.sendOk(I18nMessage.from("8230_PLEASE_HURRY_UP"))
         }
      } else if (status == 1) {
         qm.dispose()
      }
   }
}

Quest8230 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest8230(qm: qm))
   }
   return (Quest8230) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}