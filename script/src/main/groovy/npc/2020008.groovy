package npc

import config.YamlConfig
import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2020008 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int job
   def action = ["Mental": false, "Physical": false]

   def start() {
      if (cm.isQuestStarted(6192)) {
         if (cm.getParty().isEmpty()) {
            cm.sendOk(I18nMessage.from("2020008_FORM_A_PARTY"))
            cm.dispose()
            return
         }

         EventManager em = cm.getEventManager("ElnathPQ")
         if (em == null) {
            cm.sendOk(I18nMessage.from("2020008_PQ_ENCOUNTERED_ERROR"))
            cm.dispose()
            return
         }

         MaplePartyCharacter[] eli = em.getEligibleParty(cm.getParty().orElseThrow())
         if (eli.size() > 0) {
            if (!em.startInstance(cm.getParty().orElseThrow(), cm.getPlayer().getMap(), 1)) {
               cm.sendOk(I18nMessage.from("2020008_ANOTHER_PARTY_IS_CHALLENGING"))
            }
         } else {
            cm.sendOk(I18nMessage.from("2020008_PARTY_REQUIREMENTS_UNMET"))
         }

         cm.dispose()
         return
      }

      int jobBase = (cm.getJobId() / 100).intValue()
      int jobStyle = 1
      if (!(cm.getPlayer().getLevel() >= 70 && jobBase == jobStyle && cm.getJobId() % 10 == 0)) {
         if (cm.getPlayer().getLevel() >= 50 && jobBase % 10 == jobStyle) {
            status++
            action((byte) 1, (byte) 0, 1)
            return
         }

         cm.sendNext(I18nMessage.from("2020008_HI_THERE"))
         cm.dispose()
         return
      }
      if (cm.haveItem(4031058)) {
         action["Mental"] = true
      } else if (cm.haveItem(4031057)) {
         action["Physical"] = true
      }
      cm.sendSimple(I18nMessage.from("2020008_CAN_I_HELP_YOU").with(cm.getJobId() % 10 == 0 ? "\r\n#L0#I want to make the 3th job advancement." : ""))
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode == 0 && type == 0) {
         status -= 2
      } else if (mode != 1 || (status > 2 && !action["Mental"]) || status > 3) {
         if (mode == 0 && type == 1) {
            cm.sendNext(I18nMessage.from("2020008_MAKE_UP_YOUR_MIND"))
         }
         cm.dispose()
         return
      }
      if (action["Mental"]) {
         if (status == 0) {
            cm.sendNext(I18nMessage.from("2020008_GREAT_JOB_MENTAL"))
         } else if (status == 1) {
            cm.sendYesNo(I18nMessage.from("2020008_SP_MUST_BE_SPENT"))
         } else if (status == 2) {
            if (cm.getPlayer().getRemainingSp() > 0) {
               if (cm.getPlayer().getRemainingSp() > (cm.getLevel() - 70) * 3) {
                  cm.sendNext(I18nMessage.from("2020008_USE_ALL_SP"))
                  cm.dispose()
                  return
               }
            }
            if (cm.getJobId() % 10 == 0) {
               cm.gainItem(4031058, (short) -1)
               cm.changeJobById(cm.getJobId() + 1)
               cm.getPlayer().removePartyQuestItem("JBQ")
            }

            if (Math.floor(cm.getJobId() / 10) == 11) {
               cm.sendNext(I18nMessage.from("2020008_CRUSADER_SUCCESS"))
            } else if (Math.floor(cm.getJobId() / 10) == 12) {
               cm.sendNext(I18nMessage.from("2020008_WHITE_KNIGHT_SUCCESS"))
            } else {
               cm.sendNext(I18nMessage.from("2020008_DRAGON_KNIGHT_SUCCESS"))
            }
         } else if (status == 3) {
            cm.sendNextPrev(I18nMessage.from("2020008_GIVEN_SP_AND_AP"))
         }
      } else if (action["Physical"]) {
         if (status == 0) {
            cm.sendNext(I18nMessage.from("2020008_GREAT_JOB_PHYSICAL"))
         } else if (status == 1) {
            if (cm.haveItem(4031057)) {
               cm.gainItem(4031057, (short) -1)
               cm.getPlayer().setPartyQuestItemObtained("JBQ")
            }
            cm.sendNextPrev(I18nMessage.from("2020008_2ND_HALF"))
         } else if (status == 2) {
            cm.sendNextPrev(I18nMessage.from("2020008_ANSWER_EACH_AND_EVERY_QUESTION"))
         }
      } else if (cm.getPlayer().gotPartyQuestItem("JB3") && selection == 0) {
         cm.sendNext(I18nMessage.from("2020008_GO_TALK_WITH"))
         cm.dispose()
      } else if (cm.getPlayer().gotPartyQuestItem("JBQ") && selection == 0) {
         cm.sendNext(I18nMessage.from("2020008_GO_TALK_WITH_2"))
         cm.dispose()
      } else {
         if (sel == -1) {
            sel = selection
         }
         if (sel == 0) {
            if (cm.getPlayer().getLevel() >= 70 && cm.getJobId() % 10 == 0) {
               if (status == 0) {
                  cm.sendYesNo(I18nMessage.from("2020008_WELCOME"))
               } else if (status == 1) {
                  cm.getPlayer().setPartyQuestItemObtained("JB3")
                  cm.sendNext(I18nMessage.from("2020008_TESTED_STRENGTH_AND_WISDOM"))
               } else if (status == 2) {
                  cm.sendNextPrev(I18nMessage.from("2020008_MENTAL_AFTER_PHYSICAL"))
               }
            }
         } else {
            if (cm.getPlayer().getLevel() >= 50) {
               cm.sendOk(I18nMessage.from("2020008_GOOD_LUCK"))
               if (!(cm.isQuestStarted(100200) || cm.isQuestCompleted(100200))) {
                  cm.startQuest(100200)
               }
               if (YamlConfig.config.server.USE_ENABLE_SOLO_EXPEDITIONS && !cm.isQuestCompleted(100201)) {
                  cm.completeQuest(100201)
               }
            } else {
               cm.sendOk(I18nMessage.from("2020008_TOO_WEAK_FOR_ZAKUM"))
            }
            cm.dispose()
         }
      }
   }
}

NPC2020008 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2020008(cm: cm))
   }
   return (NPC2020008) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }