package quest


import scripting.quest.QuestActionManager

class Quest2315 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk("Please do not forget our plea for help.")
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline("A powerful barrier of magic, huh? Then what should we do...? If we can't find a way to break that barrier, then we can't save the princess. If it's impossible to physically break through, as you mentioned, then how about requesting help from our #bMinister of Magic#k?")
      }
      if (status == 1) {
         qm.forceStartQuest()
         qm.sendOk("Please go see him immediately. The #bMinister of Magic#k may seem a bit on the edge, but he's very knowledgeable, and I'm sure he'll know what to do.")
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
         qm.sendOk("What? You investigated the barrier at the Mushroom Forest?")
      }
      if (status == 1) {
         qm.gainExp(4000)
         qm.sendOk("Hmmm...this is interesting. It's a barrier set up by someone with a powerful force of magic, which means there's no way we can manually break through it.")
         qm.forceCompleteQuest()
         qm.dispose()
      }
   }
}

Quest2315 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2315(qm: qm))
   }
   return (Quest2315) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}