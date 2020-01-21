package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Quest21012 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 2 && mode == 0) {
            qm.sendOk("Hm... You don't think that would help? Think about it. It could help, you know...")
            qm.dispose()
            return
         } else {
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendNext("Welcome, hero! What's that? You want to know how I knew who you were? That's easy. I eavesdropped on some people talking loudly next to me. I'm sure the rumor has spread through the entire island already. Everyone knows that you've returned!")
      } else if (status == 1) {
         qm.sendNextPrev("Hm, how about trying out that sword? Wouldn't that bring back some memories? How about #bfighthing some monsters#k?")
      } else if (status == 2) {
         qm.sendAcceptDecline("Ah, I'm so sorry. I was so happy to have finally met you that I guess I got a little carried away. Whew, deep breaths. Deep breaths. Okay, I feel better now. But um...can I ask you a favor? Please?")
      } else if (status == 3) {
         qm.forceStartQuest()
         qm.sendNext("It just so happens that there are a lot of #rTutorial Murus #knear here. How about defeating just #r3 #kof them? It could help you remember a thing or two.")
      } else if (status == 4) {
         qm.sendNextPrev("Ah, you've also forgotten how to use your skills? #bPlace skills in the quick slots for easy access. #kYou can also place consumable items in the slots, so use the slots to your advantage.")
      } else if (status == 5) {
         qm.guideHint(17)
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            qm.sendNext("What? You don't want the potion?")
            qm.dispose()
            return
         } else {
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendOk("Hm... Your expression tells me that the exercise didn't jog any memories. But don't you worry. They'll come back, eventually. Here, drink this potion and power up!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v2000022# 10 #t2000022#\r\n#v2000023# 10 #t2000023#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 57 exp")
      } else if (status == 1) {
         if (qm.isQuestCompleted(21012)) {
            MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("UNKNOWN_ERROR"))
         } else if (qm.canHold(2000022) && qm.canHold(2000023)) {
            qm.forceCompleteQuest()
            qm.gainExp(57)
            qm.gainItem(2000022, (short) 10)
            qm.gainItem(2000023, (short) 10)
            qm.sendOk("#b(Even if you're really the hero everyone says you are... What good are you without any skills?)", (byte) 3)
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("INVENTORY_FULL_ERROR"))
            qm.dispose()
         }
      } else if (status == 2) {
         qm.dispose()
      }
   }
}

Quest21012 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21012(qm: qm))
   }
   return (Quest21012) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}