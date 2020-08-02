package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21200 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0 && type == 12) {
            qm.sendNext(I18nMessage.from("21200_COME_BACK"))
         }
         qm.dispose()
         return
      }
      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("21200_HOW_IS_THE_TRAINING"))
      } else if (status == 1) {
         qm.sendOk(I18nMessage.from("21200_ACTING_STRANGE"))
      } else if (status == 2) {
         qm.startQuest()
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0 && type == 1) {
            qm.sendNext(I18nMessage.from("21200_AT_LEAST_YOU_TRIED"))
         }
         qm.dispose()
         return
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("21200_VOOM_VOOM"))
      } //Giant Polearm
      else if (status == 1) {
         qm.sendNextPrev("#b(The #p1201001# is producing an undulating echo. But who is that boy standing over there?)", (byte) 2)
      } else if (status == 2) {
         qm.sendNextPrev("#b(You've never seen him before. He doesn't look human.)", (byte) 2)
      } else if (status == 3) {
         qm.sendNextPrev(I18nMessage.from("21200_DID_YOU_NOT_HEAR_ME"))
      } else if (status == 4) {
         qm.sendNextPrev("#b(Hm? Who's voice was that? It sounds like an angry boy...)", (byte) 2)
      } else if (status == 5) {
         qm.sendNextPrev(I18nMessage.from("21200_ONLY_MASTER_HAD_TO"))
      } else if (status == 6) {
         qm.sendNextPrev("Who...are you?", (byte) 2)
      } else if (status == 7) {
         qm.sendNextPrev(I18nMessage.from("21200_DO_YOU_HEAR_ME_NOW"))
      } else if (status == 8) {
         qm.sendNextPrev("#b(...#p1201002#? A #p1201001# can talk?)", (byte) 2)
      } else if (status == 9) {
         qm.sendNextPrev(I18nMessage.from("21200_SUSPICIOUS_LOOK"))
      } else if (status == 10) {
         qm.sendNextPrev("I'm so sorry, but I can't remember a thing.", (byte) 2)
      } else if (status == 11) {
         qm.sendYesNo(I18nMessage.from("21200_SORRY_DOES_NOT_CUT_IT"))
      } else if (status == 12) {
         qm.completeQuest()
         qm.sendNext("#b(The voice that claims to be #p1201002# the #p1201001# is yelling in frustration. You don't think this conversation is going anywhere. You better go talk to #p1201000# first.)", (byte) 2)
         //qm.sendNoExit("#b(The voice that claims to be #p1201002# the #p1201001# is yelling in frustration. You don't think this conversation is going anywhere. You better go talk to #p1201000# first.)", true);
      } else if (status == 13) {
         //qm.showVideo("Maha");
         qm.dispose()
      }
   }
}

Quest21200 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21200(qm: qm))
   }
   return (Quest21200) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}