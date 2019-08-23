package quest


import scripting.quest.QuestActionManager

class Quest6033 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            qm.sendNext("Hm, so you claim to have brought the #b#t4260003##k? Ok, let's take a look into it.")
         } else if (status == 1) {
            if (qm.getQuestProgress(6033) == 1 && qm.haveItem(4260003, 1)) {
               qm.sendNext("You indeed have crafted a fine piece of Monster Crystal, I see. You passed! Now, I shall teach you the next steps of the Maker skill. Keep the monster crystal with you as well, it's your work.")
            } else {
               qm.sendNext("Hey, what's wrong? I did tell you to make a monster crystal to pass my test, didn't I? Buying one or crafting before the start of the test is NOT part of the deal. Go craft me an #b#t4260003##k.")
               qm.dispose()
            }
         } else {
            int skillid = Math.floor(qm.getPlayer().getJob().getId() / 1000).intValue() * 10000000 + 1007
            qm.teachSkill(skillid, (byte) 2, (byte) 3, -1)

            qm.gainExp(230000)
            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest6033 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest6033(qm: qm))
   }
   return (Quest6033) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}