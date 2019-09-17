package quest


import scripting.quest.QuestActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Quest21011 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            qm.sendOk("Ah, okay. I understand. Heroes are very busy. *Sniff...* If you ever get any free time, though...")
            qm.dispose()
            return
         } else {
            qm.dispose()
            return
         }
      }

      if (status == 0) {
         qm.sendNext("Wait, are you... No way.... Are you the hero that #p1201000# has been talking about all this time?! #p1201000#! Don't just nod... Tell me! Is this the hero you've been waiting for?! ")
      } else if (status == 1) {
         qm.sendNextPrev("   #i4001171#")
      } else if (status == 2) {
         qm.sendNextPrev("I'm sorry. I'm just so overcome with emotions... *Sniff sniff* My goodness, I'm starting to tear up. You must be so happy, #p1201000#.")
      } else if (status == 3) {
         qm.sendAcceptDecline("Wait a minute... You're not carrying any weapons. From what I've heard, each of the heroes had a special weapon. Oh, you must have lost it during the battle against the Black Mage.")
      } else if (status == 4) {
         qm.forceStartQuest()
         qm.sendOk("My brother #bPuir #kis just down the street, and he's been dying to meet you! I know you're busy, but could you please stop by and say hello to Puir? Please...")
      } else if (status == 5) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            qm.sendNext("*sniff sniff* Isn't this sword good enough for you, just for now? I'd be so honored...")
            qm.dispose()
         } else {
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendNext("Wait, are you... No way... Are you the hero that Lilin has been talking about all this time?! Lilin! Don't just nod... Tell me! Is this the hero you've been waiting for?!")
      } else if (status == 1) {
         qm.sendNextPrev("#i4001171#")
      } else if (status == 2) {
         qm.sendNextPrev("I'm sorry. I'm just so overcome with emotions... *Sniff sniff* My goodness, I'm starting to tear up. You must be so happy, Lilin.")
      } else if (status == 3) {
         qm.sendNextPrev("Wait a minute... You're not carrying any weapons. From what I've hear, each of the heroes had a special weapon. Oh, you must have lost it during the battle against the Black Mage.")
      } else if (status == 4) {
         qm.sendYesNo("This isn't good enough to replace your weapon, but #bcarry this sword with you for now#k. It's my gift to you. A hero can't be walking around empty-handed.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v1302000# 1 #t1302000#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 35 exp")
      } else if (status == 5) {
         if (qm.isQuestCompleted(21011)) {
            MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.POP_UP, "Unknown Error")
         } else if (qm.canHold(1302000)) {
            qm.gainItem(1302000, (short) 1)
            qm.gainExp(35)
            qm.forceCompleteQuest()
            qm.sendNext("#b(Your skills are nowhere close to being hero-like... But a sword? Have you ever even held a sword in your lifetime? You can't remember... How do you even equip it?)", (byte) 3)
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.POP_UP, "Your inventory is full")
         }
      } else if (status == 6) {
         qm.guideHint(16)
         qm.dispose()
      }
   }
}

Quest21011 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21011(qm: qm))
   }
   return (Quest21011) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}