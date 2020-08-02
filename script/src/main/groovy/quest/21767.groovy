package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21767 {
   QuestActionManager qm
   int status = -1
   boolean canStart

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (status == 0) {
         if (qm.haveItem(4032423, 1)) {
            qm.forceStartQuest()
            qm.dispose()
            return
         }

         canStart = qm.canHold(4032423, 1)
         if (!canStart) {
            qm.sendNext(I18nMessage.from("21767_OPEN_ETC_SPACE"))
            return
         }

         qm.sendNext(I18nMessage.from("21767_BETTER_TAKE_IT_TO_JOHN"))
      } else if (status == 1) {
         if (canStart) {
            qm.gainItem(4032423, (short) 1)
            qm.forceStartQuest()
         }

         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21767 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21767(qm: qm))
   }
   return (Quest21767) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}