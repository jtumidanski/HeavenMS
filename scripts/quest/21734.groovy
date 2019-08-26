package quest


import scripting.quest.QuestActionManager

class Quest21734 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
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
            qm.sendNext("Hello there, Aran. We received a report that the Puppeteer, one of the members of the Black Wing, is currently based #bsomewhere on the deep forest of Sleepywood#k. Your mission is to enter the place and defeat him there, once for all.")
         } else {
            qm.forceStartQuest()
            qm.dispose()
         }
      }
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
            qm.sendNext("You made it, Aran! The Puppeteer now will not disturb the peace at Victoria Island any longer. Furthermore, now we could clearly investigate the doings of the Black Wing here, at Victoria.")
         } else if (status == 1) {
            qm.sendNext("They were after the #bcrystal seal of Victoria#k. These seals are what repels the Black Mage to further taking the continents into his grasp at once. Each continent has one, Victoria's now is safe and sound.")
         } else if (status == 2) {
            qm.sendNext("For your bravery inputted on these series of missions, I will now reward you properly. Behold, the #rCombo Drain#k Skill: that let's you heal back a portion of damage dealt to the monsters.")
         } else if (status == 3) {
            qm.forceCompleteQuest()
            qm.gainExp(12500)
            qm.teachSkill(21100005, (byte) 0, (byte) 20, -1) // combo drain
            qm.dispose()
         }
      }
   }
}

Quest21734 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21734(qm: qm))
   }
   return (Quest21734) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}