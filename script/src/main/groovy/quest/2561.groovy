package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2561 {
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
         qm.sendNext(I18nMessage.from("2561_OOK_OOK"))
      } else if (status == 1) {
         qm.sendNextPrev("I remember...I was on my way to Maple Island, to become an Explorer... What happened? What's going on?", (byte) 2)
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("2561_OOK_OOK_OOK_OOK"))
      } else if (status == 3) {
         qm.sendNextPrev("I was talking to the captain, and admiring the scenery, and... Balrog! Balrog attacked the ship! So... Did I fall overboard? Then, why am I alive? I know #bI can swim#k, but can I swim while unconscious? Maybe I can. Maybe I'm a natural swimmer!", (byte) 2)
      } else if (status == 4) {
         qm.sendNext(I18nMessage.from("2561_ANGRILY"))
      } else if (status == 5) {
         qm.sendNextPrev("Huh? Why are you waving your arms like that? Are you trying to tell me something? (The monkey took an apple out of the nearby chest. It looks delicious. But, what is he trying to tell you?)\r\n\r\n#i2010000#", (byte) 2)
      } else if (status == 6) {
         qm.sendAcceptDecline(I18nMessage.from("2561_FRUSTRATED"))
      } else if (status == 7) {
         if (mode == 0) {//decline
            qm.sendNext("The thing is, I don't like apples... Sorry, but no thanks.", (byte) 2)
            qm.dispose()
         } else {
            if (!qm.isQuestStarted(2561)) {//seems that hp is not changed o.o
               qm.gainItem(2010000, true)
               qm.forceStartQuest()
            }
            qm.sendNext("(You have received a delicious-looking apple. You should eat it. Now...how do you open your Inventory? Was it the #bI#k key...?)", (byte) 2)
         }
      } else if (status == 8) {
         qm.showInfo("UI/tutorial.img/28")
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest2561 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2561(qm: qm))
   }
   return (Quest2561) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}