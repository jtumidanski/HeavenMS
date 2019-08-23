package quest


import scripting.quest.QuestActionManager

class Quest2335 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || (mode == 0 && status == 0)) {
         qm.dispose()
         return
      } else if (mode == 0) {
         status--
      } else {
         status++
      }


      if (status == 0) {
         qm.sendNext("This is not the end, #b#h ##k. Minions of the #bPrime Minister#k can still be found scattered throughout the castle.")
      } else if (status == 1) {
         qm.sendAcceptDecline("From what I've heard, there is a place near #bSkyscraper 3#k where a group of Prime Minister's minions can be found. I've picked up a key that the Prime Minister dropped the other day. Here, use this key.")
      } else if (status == 2) {
         if (qm.canHold(4032405)) {
            qm.gainItem(4032405, (short) 1)
            qm.sendNext("For one last time, good luck.")
         } else {
            qm.sendOk("Please have a free space in your ETC inventory.")
            qm.dispose()
         }
      } else if (status == 3) {
         qm.forceStartQuest()
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest2335 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2335(qm: qm))
   }
   return (Quest2335) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}