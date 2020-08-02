package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest7103 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.sendOk(I18nMessage.from("7103_OH_REALLY"))
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.sendOk(I18nMessage.from("7103_OH_REALLY"))
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            qm.sendYesNo(I18nMessage.from("7103_ARE_YOU_READY"))
         } else if (status == 1) {
            qm.sendNext(I18nMessage.from("7103_I_WILL_EXPLAIN"))
         } else if (status == 2) {
            qm.sendNextPrev(I18nMessage.from("7103_ENTER_THE_ROOM"))
         } else if (status == 3) {
            if (!qm.haveItem(4031179, 1)) {
               if (!qm.canHold(4031179, 1)) {
                  qm.sendOk(I18nMessage.from("7103_ETC_SPACE_NEEDED"))
                  qm.dispose()
                  return
               }

               qm.gainItem(4031179, (short) 1)
            }

            qm.sendAcceptDecline(I18nMessage.from("7103_DROP_THE"))
         } else if (status == 4) {
            qm.forceStartQuest()
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest7103 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest7103(qm: qm))
   }
   return (Quest7103) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}