package server.life;

public record MobAttackInfo(Integer mobId, Integer attackId, Boolean deadlyAttack, Integer mpBurn, Integer diseaseSkill,
                            Integer diseaseLevel, Integer mpCon) {
}
