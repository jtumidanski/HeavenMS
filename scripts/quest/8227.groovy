package quest


import scripting.quest.QuestActionManager

class Quest8227 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk("Come on, the city really needs you cooperating on this one!")
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline("Hey buddy! Nice timing. There is this communique I've been able to swipe from the officials at the Keep, however it's information is encrypted. I have no use for this as it is like this. So, will you transport this to John and see if he can decode this?")
      } else if (status == 1) {
         if (qm.canHold(4032032, 1)) {
            qm.gainItem(4032032, (short) 1)
            qm.sendOk("Very well, I'm counting on you on this one.")
            qm.forceStartQuest()
         } else {
            qm.sendOk("Hey. There's no slot on your ETC.")
         }
      } else if (status == 2) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         if (qm.haveItem(4032032, 1)) {
            qm.gainItem(4032032, (short) -1)
            qm.sendOk("Oh you brought a letter from the Keep?! Neat! Let me check if I can decode that right now.")
            qm.forceCompleteQuest()
         } else {
            qm.sendOk("You don't brought the coded letter Jack said? Come on, kid, we need that to decipher our enemies' next step!")
         }
      } else if (status == 1) {
         qm.dispose()
      }
   }
}

Quest8227 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest8227(qm: qm))
   }
   return (Quest8227) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}