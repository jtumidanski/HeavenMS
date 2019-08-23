package quest


import scripting.quest.QuestActionManager

class Quest3382 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (qm.haveItem(4001159, 25) && qm.haveItem(4001160, 25) && !qm.haveItemWithId(1122010, true)) {
         if (qm.canHold(1122010)) {
            qm.gainItem(4001159, (short) -25)
            qm.gainItem(4001160, (short) -25)
            qm.gainItem(1122010, (short) 1)

            qm.sendOk("Thank you for retrieving the marbles. Accept this pendant as a token of my appreciation.")
         } else {
            qm.sendNext("Free a slot on your EQUIP tab before claiming a prize.")
            qm.dispose()
            return
         }
      } else if (qm.haveItem(4001159, 10) && qm.haveItem(4001160, 10)) {
         if (qm.canHold(2041212)) {
            qm.gainItem(4001159, (short) -10)
            qm.gainItem(4001160, (short) -10)
            qm.gainItem(2041212, (short) 1)

            qm.sendOk("Thank you for retrieving the marbles. This rock, that I am giving to you, can be used to improve the stats on the #b#t1122010##k. Take it as a token of my appreciation and use it wisely.")
         } else {
            qm.sendNext("Free a slot on your USE tab before claiming a prize.")
            qm.dispose()
            return
         }
      } else {
         qm.sendNext("I need at least #b10 of both #t4001159# and #t4001160##k to reward you appropriately. If you happen to come with #b25 of these#k instead, I can reward you with a valuable gear. Fare well.")
         qm.dispose()
         return
      }

      qm.forceCompleteQuest()
      qm.dispose()
   }
}

Quest3382 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3382(qm: qm))
   }
   return (Quest3382) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}