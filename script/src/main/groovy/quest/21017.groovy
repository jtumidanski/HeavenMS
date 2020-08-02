package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21017 {
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
         qm.sendNext("It seems like you're warmed up now. This is when rigorous training can really help you build a strong foundation. Let's proceed with the Basic Training, shall we?", (byte) 8)
      } else if (status == 1) {
         qm.sendNextPrev("Go defeat some #r#o0100133#s#k in #b#m140020200##k this time. I think about  #r20#k should do it. Go on ahead and... Hm? Do you have something you'd like to say?", (byte) 8)
      } else if (status == 2) {
         qm.sendNextPrev("Isn't the number getting bigger and bigger?", (byte) 2)
      } else if (status == 3) {
         qm.sendNextPrev("Of course it is. What, are you not happy with 20? Would you like to defeat 100 of them instead? Oh, how about 999 of them? Someone in Sleepywood would be able to do it easily. After all, we are training...", (byte) 8)
      } else if (status == 4) {
         qm.sendNextPrev("Oh no, no, no. Twenty is plenty.", (byte) 2)
      } else if (status == 5) {
         qm.sendAcceptDecline(I18nMessage.from("21017_DO_NOT_BE_SO_MODEST"))
      } else if (status == 6) {
         if (mode == 0 && type == 15) {
            qm.sendNext("#b(You declined out of fear, but it's not like you can run away like this. Take a big breath, calm down, and try again.)#k", (byte) 2)
            qm.dispose()
         } else {
            if (!qm.isQuestStarted(21017)) {
               qm.forceStartQuest()
            }
            qm.sendNext("#b(You accepted, thinking you might end up having to 999 of them if you let her keep talking.)#k", (byte) 2)
         }
      } else if (status == 7) {
         qm.sendNextPrev("Please go ahead and slay 20 #o0100133#s.", (byte) 8)
      } else if (status == 8) {
         qm.showInfo("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3")
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21017 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21017(qm: qm))
   }
   return (Quest21017) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}