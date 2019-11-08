package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "playernpcs_field")
public class PlayerNpcField implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer id;

   @Column(nullable = false)
   private Integer world;

   @Column(nullable = false)
   private Integer map;

   @Column(nullable = false)
   private Integer step;

   @Column(nullable = false)
   private Integer podium;

   public PlayerNpcField() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getWorld() {
      return world;
   }

   public void setWorld(Integer world) {
      this.world = world;
   }

   public Integer getMap() {
      return map;
   }

   public void setMap(Integer map) {
      this.map = map;
   }

   public Integer getStep() {
      return step;
   }

   public void setStep(Integer step) {
      this.step = step;
   }

   public Integer getPodium() {
      return podium;
   }

   public void setPodium(Integer podium) {
      this.podium = podium;
   }
}
