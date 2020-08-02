package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21738 {
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
            qm.sendNext("What is it? I usually don't welcome uninvited guests, but you have a mysterious aura that makes me curious about what you have to say.", (byte) 9)
         } else if (status == 1) {
            qm.sendNext("(You tell her about Giant Nependeath.)", (byte) 3)
         } else if (status == 2) {
            qm.sendNext("Giant Nependeath? It's definitely a big problem, but I don't think it's enough to really affect Orbis. Wait, where did you say the Giant Nependeath was, again?", (byte) 9)
         } else if (status == 3) {
            qm.sendNext("Neglected Strolling Path.", (byte) 3)
         } else if (status == 4) {
            qm.sendNext("...Neglected Strolling Path? If Giant Nependeath is there, someone is trying to enter Sealed Garden! But why? And more importantly, who?", (byte) 9)
         } else if (status == 5) {
            qm.sendNext("Sealed Garden?", (byte) 3)
         } else if (status == 6) {
            qm.sendAcceptDecline("I can't tell you about Sealed Garden. If you want to find out, I must first see whether you are worthy of the information. Do you mind if I look into your fate?", (byte) 9)
         } else if (status == 7) {
            qm.sendOk(I18nMessage.from("21738_GIVE_ME_A_SECOND"))
         } else if (status == 8) {
            qm.forceStartQuest()
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21738 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21738(qm: qm))
   }
   return (Quest21738) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}