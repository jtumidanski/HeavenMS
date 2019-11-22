package net.server.channel.services;

import net.server.channel.services.task.BaseService;
import net.server.channel.services.task.EventService;
import net.server.channel.services.task.FaceExpressionService;
import net.server.channel.services.task.MobAnimationService;
import net.server.channel.services.task.MobClearSkillService;
import net.server.channel.services.task.MobMistService;
import net.server.channel.services.task.MobStatusService;
import net.server.channel.services.task.OverallService;

public enum ServiceType {
   MOB_STATUS(MobStatusService.class),
   MOB_ANIMATION(MobAnimationService.class),
   MOB_CLEAR_SKILL(MobClearSkillService.class),
   MOB_MIST(MobMistService.class),
   FACE_EXPRESSION(FaceExpressionService.class),
   EVENT(EventService.class),
   OVERALL(OverallService.class);

   private Class<? extends BaseService> s;

   private ServiceType(Class<? extends BaseService> service) {
      s = service;
   }

   public Service createService() {
      return new Service(s);
   }
}
