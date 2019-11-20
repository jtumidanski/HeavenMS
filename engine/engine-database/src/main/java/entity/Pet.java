package entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "pets")
public class Pet implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer petId;

   @Column
   private String name;

   @Column(nullable = false)
   private Integer level;

   @Column(nullable = false)
   private Integer closeness;

   @Column(nullable = false)
   private Integer fullness;

   @Column(nullable = false)
   private Integer summoned;

   @Column(nullable = false)
   private Integer flag;

   @OneToMany(targetEntity = PetIgnore.class, mappedBy = "pet", cascade = CascadeType.ALL)
   private List<PetIgnore> petIgnoreList;

   public Pet() {
   }

   public Integer getPetId() {
      return petId;
   }

   public void setPetId(Integer petId) {
      this.petId = petId;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Integer getLevel() {
      return level;
   }

   public void setLevel(Integer level) {
      this.level = level;
   }

   public Integer getCloseness() {
      return closeness;
   }

   public void setCloseness(Integer closeness) {
      this.closeness = closeness;
   }

   public Integer getFullness() {
      return fullness;
   }

   public void setFullness(Integer fullness) {
      this.fullness = fullness;
   }

   public Integer getSummoned() {
      return summoned;
   }

   public void setSummoned(Integer summoned) {
      this.summoned = summoned;
   }

   public Integer getFlag() {
      return flag;
   }

   public void setFlag(Integer flag) {
      this.flag = flag;
   }
}
