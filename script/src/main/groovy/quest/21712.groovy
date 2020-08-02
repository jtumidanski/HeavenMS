package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21712 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || mode == 0 && type > 0) {
         qm.dispose()
         return
      }

      if (mode == 1) {
         status++
      } else {
         if (status == 2) {
            qm.sendNext(I18nMessage.from("21712_I_WILL_EXPLAIN_AGAIN"))
            qm.dispose()
            return
         }
         status--
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("21712_CAN_ONLY_BE_HEARD"))
      } else if (status == 1) {
         qm.sendAcceptDecline(I18nMessage.from("21712_TURNED_CYNICAL"))
      } else if (status == 2) {
         qm.forceStartQuest()
         qm.sendNext("I wonder what triggered this in the first place. There is no way this puppet was naturally created, which means someone planned this. I should keep an eye on the #o1210102#s.", (byte) 9)
      } else if (status == 3) {
         qm.sendPrev("#b(You were able to find out what caused the changes in the #o1210102#s. You should report to #p1002104# and deliver the information you've gathered.)#k", (byte) 2)
      } else if (status == 4) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.dispose()
   }
}

Quest21712 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21712(qm: qm))
   }
   return (Quest21712) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}