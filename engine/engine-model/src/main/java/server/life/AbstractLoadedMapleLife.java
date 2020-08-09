package server.life;

import server.maps.AbstractAnimatedMapleMapObject;

public abstract class AbstractLoadedMapleLife extends AbstractAnimatedMapleMapObject {
   private final Integer id;

   private Integer f;

   private Boolean hide;

   private Integer fh;

   private Integer startFh;

   private Integer cy;

   private Integer rx0;

   private Integer rx1;

   public AbstractLoadedMapleLife(Integer id) {
      this.id = id;
   }

   public AbstractLoadedMapleLife(AbstractLoadedMapleLife life) {
      this(life.id());
      this.f = life.f();
      this.hide = life.hide();
      this.fh = life.fh();
      this.startFh = life.startFh();
      this.cy = life.cy();
      this.rx0 = life.rx0();
      this.rx1 = life.rx1();
   }

   public Integer id() {
      return id;
   }

   public Integer f() {
      return f;
   }

   public void setF(Integer f) {
      this.f = f;
   }

   public Boolean hide() {
      return hide;
   }

   public void setHide(Boolean hide) {
      this.hide = hide;
   }

   public Integer fh() {
      return fh;
   }

   public void setFh(Integer fh) {
      this.fh = fh;
   }

   public Integer startFh() {
      return startFh;
   }

   public void setStartFh(Integer startFh) {
      this.startFh = startFh;
   }

   public Integer cy() {
      return cy;
   }

   public void setCy(Integer cy) {
      this.cy = cy;
   }

   public Integer rx0() {
      return rx0;
   }

   public void setRx0(Integer rx0) {
      this.rx0 = rx0;
   }

   public Integer rx1() {
      return rx1;
   }

   public void setRx1(Integer rx1) {
      this.rx1 = rx1;
   }
}
