package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21100 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && type == 0) {
         status--
      } else if (mode == -1) {
         qm.dispose()
         return
      } else {
         status++
      }
      if (status == 0) {
         qm.sendNext("There isn't much record left of the heroes that fought against the Black Magician. Even in the Book of Prophecy, the only information available is that there were five of them. There is nothing about who they were or what they looked like. Is there anything you remember? Anything at all?", (byte) 8)
      } else if (status == 1) {
         qm.sendNextPrev("I don't remember a thing...", (byte) 2)
      } else if (status == 2) {
         qm.sendNextPrev("As I expected. Of course, the curse of the Black Magician was strong enough to wipe out all of your memory. But even if that's the case, there has got to be a point where the past will uncover, especially now that we are certain you are one of the heroes. I know you've lost your armor and weapon during the battle but... Oh, yes, yes. I almost forgot! Your #bweapon#k!", (byte) 8)
      } else if (status == 3) {
         qm.sendNextPrev("My weapon?", (byte) 2)
      } else if (status == 4) {
         qm.sendNextPrev("I found an incredible weapon while digging through blocks of ice a while back. I figured the weapon belonged to a hero, so I brought it to town and placed it somewhere in the center of the town. Haven't you seen it? #bThe #p1201001##k... \r\r#i4032372#\r\rIt looks like this...", (byte) 8)
      } else if (status == 5) {
         qm.sendNextPrev("Come to think of it, I did see a #p1201001# in town.", (byte) 2)
      } else if (status == 6) {
         qm.sendAcceptDecline(I18nMessage.from("21100_WEAPON_WILL_RECOGNIZE_ITS_RIGHTFUL_OWNER"))
      } else if (status == 7) {
         if (mode == 0 && type == 15) {
            qm.sendNext("What's stopping you? I promise, I won't be disappointed even if the #p1201001# shows no reaction to you. Please, rush over there and grab the #p1201001#. Just #bclick#k on it.", (byte) 8)
         } else {
            qm.forceCompleteQuest()
            qm.sendOk("If the #p1201001# reacts to you, then we'll know that you're #bAran#k, the hero that wielded a #p1201001#.", (byte) 8)
            qm.showIntro("Effect/Direction1.img/aranTutorial/ClickPoleArm")
         }
      } else if (status == 8) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21100 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21100(qm: qm))
   }
   return (Quest21100) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}