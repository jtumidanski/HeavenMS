package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2081100 {
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
            if (cm.getLevel() < 120 || Math.floor(cm.getJobId() / 100) != 1) {
               cm.sendOk(I18nMessage.from("2081100_DO_NOT_BOTHER_ME"))
               cm.dispose()
            } else if (!cm.isQuestCompleted(6904)) {
               cm.sendOk(I18nMessage.from("2081100_NOT_YET_PASSED"))
               cm.dispose()
            } else if (cm.getJobId() % 100 % 10 != 2) {
               cm.sendYesNo(I18nMessage.from("2081100_DID_A_MARVELLOUS_JOB"))
            } else {
               cm.sendSimple(I18nMessage.from("2081100_I_CAN_TEACH_YOU"))
            }
         } else if (status == 1) {
            if (mode >= 1 && cm.getJobId() % 100 % 10 != 2) {
               if (cm.canHold(2280003, 1)) {
                  cm.changeJobById(cm.getJobId() + 1)
                  if (cm.getJobId() == 112) {
                     cm.teachSkill(1121001, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(1120004, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(1121008, (byte) 0, (byte) 10, -1)
                  } else if (cm.getJobId() == 122) {
                     cm.teachSkill(1221001, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(1220005, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(1221009, (byte) 0, (byte) 10, -1)
                  } else if (cm.getJobId() == 132) {
                     cm.teachSkill(1321001, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(1320005, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(1321007, (byte) 0, (byte) 10, -1)
                  }
                  cm.gainItem(2280003, (short) 1)
               } else {
                  cm.sendOk(I18nMessage.from("2081100_HAVE_ONE_USE_SLOT"))
               }
            } else if (mode >= 0 && cm.getJobId() % 100 % 10 == 2) {
               // TEMP until I can get the quest fixed...
               if (cm.getJobId() == 112) {
                  if (cm.getPlayer().getSkillLevel(1121010) == 0) {
                     cm.teachSkill(1121010, (byte) 0, (byte) 10, -1)
                  }
                  if (cm.getPlayer().getSkillLevel(1120005) == 0) {
                     cm.teachSkill(1120005, (byte) 0, (byte) 10, -1)
                  }
                  if (cm.getPlayer().getSkillLevel(1121002) == 0) {
                     cm.teachSkill(1121002, (byte) 0, (byte) 10, -1)
                  }
               } else if (cm.getJobId() == 122) {
                  if (cm.getPlayer().getSkillLevel(1221002) == 0) {
                     cm.teachSkill(1221002, (byte) 0, (byte) 10, -1)
                  }
                  if (cm.getPlayer().getSkillLevel(1221003) == 0) {
                     cm.teachSkill(1221003, (byte) 0, (byte) 10, -1)
                  }
                  if (cm.getPlayer().getSkillLevel(1221004) == 0) {
                     cm.teachSkill(1221004, (byte) 0, (byte) 10, -1)
                  }
               } else if (cm.getJobId() == 132) {
                  if (cm.getPlayer().getSkillLevel(1321002) == 0) {
                     cm.teachSkill(1321002, (byte) 0, (byte) 10, -1)
                  }
                  if (cm.getPlayer().getSkillLevel(1320008) == 0) {
                     cm.teachSkill(1320008, (byte) 0, (byte) 10, -1)
                  }
                  if (cm.getPlayer().getSkillLevel(1320009) == 0) {
                     cm.teachSkill(1320009, (byte) 0, (byte) 10, -1)
                  }
               }
               cm.sendOk(I18nMessage.from("2081100_IT_IS_DONE"))
            }

            cm.dispose()
         }
      }
   }
}

NPC2081100 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2081100(cm: cm))
   }
   return (NPC2081100) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }