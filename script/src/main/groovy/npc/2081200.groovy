package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2081200 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            if (cm.getLevel() < 120 || Math.floor(cm.getJobId() / 100) != 2) {
               cm.sendOk(I18nMessage.from("2081200_DO_NOT_BOTHER_ME"))
               cm.dispose()
            } else if (!cm.isQuestCompleted(6914)) {
               cm.sendOk(I18nMessage.from("2081200_NOT_YET_PASSED"))
               cm.dispose()
            } else if (cm.getJobId() % 100 % 10 != 2) {
               cm.sendYesNo(I18nMessage.from("2081200_DID_A_MARVELLOUS_JOB"))
            } else {
               cm.sendSimple(I18nMessage.from("2081200_I_CAN_TEACH_YOU"))
               //cm.dispose();
            }
         } else if (status == 1) {
            if (mode >= 1 && cm.getJobId() % 100 % 10 != 2) {
               if (cm.canHold(2280003, 1)) {
                  cm.changeJobById(cm.getJobId() + 1)
                  if (cm.getJobId() == 212) {
                     cm.teachSkill(2121001, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(2121002, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(2121006, (byte) 0, (byte) 10, -1)
                  } else if (cm.getJobId() == 222) {
                     cm.teachSkill(2221001, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(2221002, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(2221006, (byte) 0, (byte) 10, -1)
                  } else if (cm.getJobId() == 232) {
                     cm.teachSkill(2321001, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(2321002, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(2321005, (byte) 0, (byte) 10, -1)
                  }
                  cm.gainItem(2280003, (short) 1)
               } else {
                  cm.sendOk(I18nMessage.from("2081200_HAVE_ONE_USE_SLOT"))
               }
            } else if (mode >= 1 && cm.getJobId() % 100 % 10 == 2) {
               if (cm.getJobId() == 212) {
                  if (cm.getPlayer().getSkillLevel(2121007) == 0) {
                     cm.teachSkill(2121007, (byte) 0, (byte) 10, -1)
                  }
                  if (cm.getPlayer().getSkillLevel(2121005) == 0) {
                     cm.teachSkill(2121005, (byte) 0, (byte) 10, -1)
                  }
                  if (cm.getPlayer().getSkillLevel(2121005) == 0) {
                     cm.teachSkill(2121005, (byte) 0, (byte) 10, -1)
                  }
               } else if (cm.getJobId() == 222) {
                  if (cm.getPlayer().getSkillLevel(2221007) == 0) {
                     cm.teachSkill(2221007, (byte) 0, (byte) 10, -1)
                  }
                  if (cm.getPlayer().getSkillLevel(2221005) == 0) {
                     cm.teachSkill(2221005, (byte) 0, (byte) 10, -1)
                  }
                  if (cm.getPlayer().getSkillLevel(2221003) == 0) {
                     cm.teachSkill(2221003, (byte) 0, (byte) 10, -1)
                  }
               } else if (cm.getJobId() == 232) {
                  if (cm.getPlayer().getSkillLevel(2321008) < 1) {
                     cm.teachSkill(2321008, (byte) 0, (byte) 10, -1)
                  } // Genesis
                  if (cm.getPlayer().getSkillLevel(2321006) < 1) {
                     cm.teachSkill(2321006, (byte) 0, (byte) 10, -1)
                  } // res
               }
               cm.sendOk(I18nMessage.from("2081200_IT_IS_DONE"))
            }
            cm.dispose()
         }
      }
   }
}

NPC2081200 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2081200(cm: cm))
   }
   return (NPC2081200) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }