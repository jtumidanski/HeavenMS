package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest22507 {
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
         qm.sendNext(I18nMessage.from("22507_I_KNEW_IT"))
      } else if (status == 1) {
         qm.sendNextPrev("#bI see. How did we end up in this pact anyway?", (byte) 2)
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("22507_I_CANNOT_REMEMBER"))
      } else if (status == 3) {
         qm.sendNextPrev("#b#b(Wait! That sounds just like that one dream you had... Did the two of you meet in a dream? Is it possible that the giant Dragon you saw in that dream was...#p1013000#?)", (byte) 2)
      } else if (status == 4) {
         qm.sendNextPrev(I18nMessage.from("22507_YOU_AND_I_ARE_ONE"))
      } else if (status == 5) {
         qm.sendNextPrev("#bI paid a price?", (byte) 2)
      } else if (status == 6) {
         qm.sendNextPrev(I18nMessage.from("22507_DO_YOU_NOT_REMEMBER"))
      } else if (status == 7) {
         qm.sendNextPrev("#bOne in...spirit?", (byte) 2)
      } else if (status == 8) {
         qm.sendNextPrev(I18nMessage.from("22507_SPIRIT_PACT"))
      } else if (status == 9) {
         qm.sendNextPrev("#bI have no idea what you're talking about, but it sounds like a pretty big deal.", (byte) 2)
      } else if (status == 10) {
         qm.sendNextPrev(I18nMessage.from("22507_BIG_DEAL"))
      } else if (status == 11) {
         qm.sendNextPrev("#bBut it's peaceful here. There are no dangerous monsters around.", (byte) 2)
      } else if (status == 12) {
         qm.sendNextPrev(I18nMessage.from("22507_THAT_IS_NO_FUN"))
      } else if (status == 13) {
         qm.sendNextPrev("#bIt's not part of my five year plan. I'm just kidding, but seriously, I'm a farmer's kid...", (byte) 2)
      } else if (status == 14) {
         qm.sendAcceptDecline(I18nMessage.from("22507_LET_ME_TELL_YOU_THIS"))
      } else if (status == 15) {
         if (mode == 0) {
            qm.sendNext(I18nMessage.from("22507_YOU_ARE_KIDDING_ME"))
         } else {
            if (!qm.isQuestCompleted(22507)) {
               qm.forceCompleteQuest()
               qm.gainExp(810)
            }
            qm.sendNext(I18nMessage.from("22507_ALRIGHTY_THEN"))
         }
      } else if (status == 16) {
         qm.sendNextPrev("#b(You're a bit confused, but you are now traveling with Mir the Dragon. Perhaps you'll go on an adventure together, like he said.)", (byte) 2)
      } else if (status == 17) {
         qm.sendPrev(I18nMessage.from("22507_DAD_NEEDS_TO_TALK"))
      } else if (status == 18) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest22507 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest22507(qm: qm))
   }
   return (Quest22507) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}