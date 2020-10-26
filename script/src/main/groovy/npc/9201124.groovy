package npc

import constants.MapleJob
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201124 {
    NPCConversationManager cm
    int status = -1
    int sel = -1

    int map = 100000201
    String job = "Bowman"
    int jobType = 3
    String no = "Come back to me if you decided to be a #b" + job + "#k."

    def start() {
        status = -1
        action((byte) 1, (byte) 0, 0)
    }

    def action(Byte mode, Byte type, Integer selection) {
        if (mode == -1) {
            cm.sendOk(no)
            cm.dispose()
        } else {
            if (mode == 0 && type > 0) {
                cm.sendOk(no)
                cm.dispose()
            }

            if (mode == 1) {
                status++
            } else {
                status--
            }

            if (status == 0) {
                if (cm.getJob().getId() == MapleJob.BEGINNER.getId()) {
                    if (cm.getLevel() >= 10 && cm.canGetFirstJob(jobType)) {
                        cm.sendYesNo(I18nMessage.from("9201124_DO_YOU_WANT_TO_GO").with(map, job))

                    } else {
                        cm.sendOk(I18nMessage.from("9201124_IF_YOU_WANT").with(job, cm.getFirstJobStatRequirement(jobType)))

                        cm.dispose()
                    }
                } else {
                    cm.sendOk(I18nMessage.from("9201124_MUCH_STRONGER_NOW"))

                    cm.dispose()
                }
            } else if (status == 1) {
                cm.warp(map, 0)
                cm.dispose()
            }
        }
    }
}

NPC9201124 getNPC() {
    if (!getBinding().hasVariable("npc")) {
        NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
        getBinding().setVariable("npc", new NPC9201124(cm: cm))
    }
    return (NPC9201124) getBinding().getVariable("npc")
}

def start() {
    getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }