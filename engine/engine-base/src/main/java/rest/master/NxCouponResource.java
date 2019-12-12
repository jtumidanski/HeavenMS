package rest.master;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import database.DatabaseConnection;
import database.administrator.NxCouponAdministrator;


@Path("nxcoupons")
public class NxCouponResource {
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response create(NxCoupon nxCoupon) {
      DatabaseConnection.getInstance().withConnection(entityManager -> {
         int key = NxCouponAdministrator.getInstance().create(entityManager, nxCoupon.coupon(), nxCoupon.rate(), nxCoupon.activeDay(), nxCoupon.startHour(), nxCoupon.endHour());
         nxCoupon.id(key);
      });
      if (nxCoupon.id() == -1) {
         return Response.status(Response.Status.CONFLICT).build();
      }
      return Response.ok().entity(nxCoupon).build();
   }

   @Path("/bulk")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createBulk(List<NxCoupon> nxCoupons) {
      DatabaseConnection.getInstance().withConnection(entityManager ->
            nxCoupons.forEach(nxCoupon -> {
               int key = NxCouponAdministrator.getInstance().create(entityManager, nxCoupon.coupon(), nxCoupon.rate(), nxCoupon.activeDay(), nxCoupon.startHour(), nxCoupon.endHour());
               nxCoupon.id(key);
            }));
      if (nxCoupons.stream().anyMatch(nxCoupon -> nxCoupon.id() == -1)) {
         return Response.status(Response.Status.CONFLICT).build();
      }
      return Response.ok().entity(nxCoupons).build();
   }
}
