package quest


import scripting.quest.QuestActionManager

class Quest2319 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk("I know it's not a tough task, so come back to me if you're ready.")
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline("Oh, I almost forgot! What was I thinking? I need you to hand this #bSample of Killer Mushroom Spores#k to #bMinister of Magic#k and report the results.")
      } else if (status == 1) {
         qm.forceStartQuest()
         qm.gainItem(4032389, (short) 1)
         qm.sendOk("The #bMinister of Magic#k told me once the #bKiller Mushroom Spores#k is complete, that he'll want a sample of it as well. I'll give you the sample; now go please hand it in to our #bMinister of Magic.#k")
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
         qm.sendOk("Are the #bKiller Mushroom Spores#k finally completed?")
      } else if (status == 1) {
         qm.forceCompleteQuest()
         qm.gainExp(4200)
         qm.gainItem(4032389, (short) -1)
         qm.sendOk("Okay, so this is the #bKiller Mushroom Spores.#k Thank you, thank you, and please tell #bScarrs#k the same.")
      } else if (status == 2) {
         qm.dispose()
      }
   }
}

Quest2319 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2319(qm: qm))
   }
   return (Quest2319) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}