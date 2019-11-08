package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "area_info")
public class AreaInfo implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer id;

   @Column(nullable = false, name = "charid")
   private Integer characterId;

   @Column(nullable = false)
   private Integer area;

   @Column(nullable = false, length = 200)
   private String info;

   public AreaInfo() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getCharacterId() {
      return characterId;
   }

   public void setCharacterId(Integer characterId) {
      this.characterId = characterId;
   }

   public Integer getArea() {
      return area;
   }

   public void setArea(Integer area) {
      this.area = area;
   }

   public String getInfo() {
      return info;
   }

   public void setInfo(String info) {
      this.info = info;
   }
}
