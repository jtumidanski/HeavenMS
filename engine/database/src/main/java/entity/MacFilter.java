package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "macfilters")
public class MacFilter implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer macFilterId;

   @Column(nullable = false)
   private String filter;

   public MacFilter() {
   }

   public Integer getMacFilterId() {
      return macFilterId;
   }

   public void setMacFilterId(Integer macFilterId) {
      this.macFilterId = macFilterId;
   }

   public String getFilter() {
      return filter;
   }

   public void setFilter(String filter) {
      this.filter = filter;
   }
}
