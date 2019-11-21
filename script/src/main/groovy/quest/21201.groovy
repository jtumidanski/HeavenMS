package quest

import config.YamlConfig
import scripting.quest.QuestActionManager

class Quest21201 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.sendNext("Hey! At least say you tried!")
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.sendNext("Hey! At least say you tried!")
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            qm.sendNext("First you promise to defeat the Black Mage and make me a famous weapon, then you abandon me for hundreds of years, and now you're telling me you don't remember who I am? What the...?! Do you think I will let you get away with that? You're the one who begged and pined for me!")
         } //Giant Polearm
         else if (status == 1) {
            qm.sendNextPrev("I did tell #p1203000# to make a pole arm for me if I could prove my worth.", (byte) 2)
         } else if (status == 2) {
            qm.sendNextPrev("After all that begging, shouldn't you treat me with a little more love and respect? Ya know, a weapon like me's a rare and wonderful thing. I am the ultimate #p1201001# that can help you defeat the Black Mage. How could you ditch me for hundreds of years?")
         } else if (status == 3) {
            qm.sendNextPrev("Hey, I never begged for you.", (byte) 2)
         } else if (status == 4) {
            qm.sendNextPrev("What? You never begged for me? Ha! #p1203000# told me you got on your knees, begged for me in tears, and... Wait a sec. Aran! Did you just remember who I am?")
         } else if (status == 5) {
            qm.sendNextPrev("Maybe a little bit...", (byte) 2)
         } else if (status == 6) {
            qm.sendNextPrev("Aran, it is you! *Sniff sniff* Wait, *ahem* I didn't get emotional, it's just allergies. I know the Black Mage has stripped you of your abilities so you probably don't even have the strength to lift me... but at least you remember me! I'm glad that your memory's starting to return.")
         } else if (status == 7) {
            qm.sendAcceptDecline("Even though you've lost your memory, you're still my master. You endured some very tough training in the past, and I'm sure your body still remembers the skills you got through those hard times. Alright, I'll restore your abilities!")
         } else if (status == 8) {
            if (!qm.isQuestCompleted(21201)) {
               if (!qm.canHold(1142130)) {
                  qm.sendOk("Wow, your #bequip#k inventory is full. I need you to make at least 1 empty slot to complete this quest.")
                  return
               }

               qm.gainItem(1142130, true)
               qm.changeJobById(2110)

               if (YamlConfig.config.server.USE_FULL_ARAN_SKILLSET) {
                  qm.teachSkill(21100000, (byte) 0, (byte) 20, -1)   //polearm mastery
                  qm.teachSkill(21100002, (byte) 0, (byte) 30, -1)   //final charge
                  qm.teachSkill(21100004, (byte) 0, (byte) 20, -1)   //combo smash
                  qm.teachSkill(21100005, (byte) 0, (byte) 20, -1)   //combo drain
               }

               qm.completeQuest()
            }

            qm.sendNext("Your level isn't what it used to be back in your glory days, so I can't restore all of your old abilities. But the few I can restore should help you level up faster. Now hurry up and train so you can return to the old you.")
         } else if (status == 9) {
            qm.dispose()
         }
      }
   }
}

Quest21201 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21201(qm: qm))
   }
   return (Quest21201) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}