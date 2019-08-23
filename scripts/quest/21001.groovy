package quest


import scripting.quest.QuestActionManager

class Quest21001 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 15 && mode == 0) {
            qm.sendNext("*Sob* Aran has declined my request!")
            qm.dispose()
            return
         } else {
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline("*Sniff sniff* I was so scared... Please take me to Athena Pierce.")
      } else if (status == 1) {
         qm.gainItem(4001271, (short) 1)
         qm.forceStartQuest()
         qm.warp(914000300, 0)
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            qm.sendNext("What about the child? Please give me the child!")
         }

         qm.dispose()
         return
      }

      if (status == 0) {
         qm.sendYesNo("You made it back safely! What about the child?! Did you bring the child with you?!")
      } else if (status == 1) {
         qm.sendNext("Oh, what a relief. I'm so glad...", (byte) 9)
      } else if (status == 2) {
         qm.sendNextPrev("Hurry and board the ship! We don't have much time!", (byte) 3)
      } else if (status == 3) {
         qm.sendNextPrev("We don't have any time to waste. The Black Mage's forces are getting closer and closer! We're doomed if we don't leave right right this moment!", (byte) 9)
      } else if (status == 4) {
         qm.sendNextPrev("Leave, now!", (byte) 3)
      } else if (status == 5) {
         qm.sendNextPrev("Aran, please! I know you want to stay and fight the Black Mage, but it's too late! Leave it to the others and come to Victoria Island with us!", (byte) 9)
      } else if (status == 6) {
         qm.sendNextPrev("No, I can't!", (byte) 3)
      } else if (status == 7) {
         qm.sendNextPrev("Athena Pierce, why don't you leave for Victoria Island first? I promise I'll come for you later. I'll be alright. I must fight the Black Mage with the other heroes!", (byte) 3)
      } else if (status == 8) {
         qm.gainItem(4001271, (short) -1)
         qm.removeEquipFromSlot((short) -11)
         qm.forceCompleteQuest()

         qm.warp(914090010, 0) // Initialize Aran Tutorial Scenes
         qm.dispose()
      }
   }
}

Quest21001 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21001(qm: qm))
   }
   return (Quest21001) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}