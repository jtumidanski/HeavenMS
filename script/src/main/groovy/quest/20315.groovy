package quest

import constants.MapleJob
import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest20315 {
    QuestActionManager qm
    int status = -1

    def start(Byte mode, Byte type, Integer selection) {
        if (mode == -1) {
            qm.dispose()
        } else {
            if (status == 1 && mode == 0) {
                qm.sendNext(I18nMessage.from("20315_COME_BACK"))
                qm.dispose()
                return
            }
            if (mode == 1) {
                status++
            } else {
                status--
            }
            if (status == 0) {
                qm.sendNext(I18nMessage.from("20315_DOOM_US_ALL"))
            } else if (status == 1) {
                qm.sendYesNo(I18nMessage.from("20315_NEW_TITLE"))
            } else if (status == 2) {
                int nPSP = (qm.getPlayer().getLevel() - 70) * 3
                if (qm.getPlayer().getRemainingSp() > nPSP) {
                    qm.sendNext(I18nMessage.from("20315_SPEND_SP"))
                } else {
                    if (!qm.canHold(1142068)) {
                        qm.sendNext(I18nMessage.from("20315_NEED_ROOM_FOR_MEDAL"))
                    } else {
                        qm.gainItem(1142068, (short) 1)
                        qm.getPlayer().changeJob(MapleJob.THUNDER_BREAKER_3)
                        qm.completeQuest()
                        qm.sendOk(I18nMessage.from("20315_ADVANCED_KNIGHT"))
                    }
                }
            } else if (status == 3) {
                qm.dispose()
            }
        }
    }

    def end(Byte mode, Byte type, Integer selection) {

    }
}

Quest20315 getQuest() {
    if (!getBinding().hasVariable("quest")) {
        QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
        getBinding().setVariable("quest", new Quest20315(qm: qm))
    }
    return (Quest20315) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
    getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
    getQuest().end(mode, type, selection)
}