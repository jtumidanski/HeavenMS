package quest


import scripting.quest.QuestActionManager

class Quest2293 {
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
         qm.sendNext("Do you remember the last song that the Spirit of Rock played? I can think of a few songs that he may be imitating, so listen carefully and tell me which song it is. #bYou only get one chance,#k so please choose wisely.")
         qm.forceStartQuest()
      } else if (status == 1) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || (mode == 0 && status == 0)) {
         qm.dispose()
         return
      } else if (mode == 0) {
         status--
      } else {
         status++
      }

      if (status == 0) {
         qm.sendSimple("Here, I'll give you some samples. Please listen to them and choose one. Please listen carefully before making your choice.\r\n\
            \t#b#L1# Listen to song No. 1#l \r\n\
            \t#L2# Listen to Song No. 2#l \r\n\
            \t#L3# Listen to Song No. 3#l \r\n\
            \r\n\
            \t#e#L4# Enter the correct song.#l")
      } else if (status == 1) {
         if (selection == 1) {
            qm.playSound("Party1/Failed")
            qm.sendOk("Awkwardly familiar...")
            status = -1
         } else if (selection == 2) {
            qm.playSound("Coconut/Failed")
            qm.sendOk("Was it this?")
            status = -1
         } else if (selection == 3) {
            qm.playSound("quest2293/Die")
            qm.sendOk("You heard that?")
            status = -1
         } else if (selection == 4) {
            qm.sendGetNumber("Now, please tell me the answer. You only get #bone chance#k, so please choose wisely. Please enter #b1, 2, or 3#k in the window below.\r\n", 1, 1, 3)
         }
      } else if (status == 2) {
         if (selection == 1) {
            qm.sendOk("Obviously you don't enjoy music.")
         } else if (selection == 2) {
            qm.sendOk("I suppose you could get #b#eone#n#k more chance.")
         } else if (selection == 3) {
            qm.sendOk("So that was the song he was playing... Well, it wasn't my song after all, but I'm glad I can know that now with certainty. Thank you so much.")
            qm.forceCompleteQuest()
            qm.gainExp(32500)
         } else {
            qm.dispose()
         }
      } else if (status == 3) {
         qm.dispose()
      }
   }
}

Quest2293 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2293(qm: qm))
   }
   return (Quest2293) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}