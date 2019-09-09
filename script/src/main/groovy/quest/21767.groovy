package quest


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
            qm.sendNext("Please open a slot in your ETC inventory first.")
            return
         }

         qm.sendNext("#bHm, there's a medicinal substance in the box. What could this be? You better take this to John and ask him what it is.#k")
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