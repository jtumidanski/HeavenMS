package rest.master;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import database.DatabaseConnection;
import database.administrator.TempDataAdministrator;


@Path("temp")
public class TempDataResource {
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response get(@QueryParam("filter[dropper_min]") Integer dropperMin,
                       @QueryParam("filter[dropper_max]") Integer dropperMax,
                       @QueryParam("filter[dropper]") Integer dropper,
                       @QueryParam("filter[item_min]") Integer itemMin,
                       @QueryParam("filter[item_max]") Integer itemMax,
                       @QueryParam("filter[item]") Integer item,
                       @QueryParam("filter[chance_min]") Integer chanceMin,
                       @QueryParam("filter[chance]") Integer chance) {


      List<TempData> results = new ArrayList<>();
      DatabaseConnection.getInstance().withConnection(entityManager -> {

         List<Predicate> predicateList = new ArrayList<>();
         CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
         CriteriaQuery<entity.TempData> criteriaQuery = criteriaBuilder.createQuery(entity.TempData.class);
         Root<entity.TempData> root = criteriaQuery.from(entity.TempData.class);

         addComparablePredicate(predicateList, root.get("dropperId"), dropperMin, criteriaBuilder::greaterThan);
         addComparablePredicate(predicateList, root.get("dropperId"), dropperMax, criteriaBuilder::lessThanOrEqualTo);
         addComparablePredicate(predicateList, root.get("dropperId"), dropper, criteriaBuilder::equal);
         addComparablePredicate(predicateList, root.get("itemId"), itemMin, criteriaBuilder::greaterThan);
         addComparablePredicate(predicateList, root.get("itemId"), itemMax, criteriaBuilder::lessThanOrEqualTo);
         addComparablePredicate(predicateList, root.get("itemId"), item, criteriaBuilder::equal);
         addComparablePredicate(predicateList, root.get("chance"), chanceMin, criteriaBuilder::greaterThan);
         addComparablePredicate(predicateList, root.get("chance"), chance, criteriaBuilder::equal);

         Predicate[] predicateArray = predicateList.toArray(new Predicate[0]);
         TypedQuery<entity.TempData> query = entityManager.createQuery(criteriaQuery.select(root).where(predicateArray));

         query.getResultList().stream()
               .map(result -> new TempData(result.getDropperId(), result.getItemId(), result.getMinimumQuantity(),
                     result.getMaximumQuantity(), result.getQuestId(), result.getChance()))
               .forEach(results::add);
      });
      return Response.ok().entity(new TempDataResponse(results)).build();
   }

   /**
    * Adds a predicate to the supplied list of predicates if the value exists.
    *
    * @param predicates    a list of predicates to add to
    * @param attributePath the path to the attribute
    * @param value         the value the attribute should be compared to
    * @param function      a function which builds the predicate
    * @param <Y>           the type of value being compared
    */
   private <Y extends Comparable<? super Y>> void addComparablePredicate(List<Predicate> predicates, javax.persistence.criteria.Path<Y> attributePath, Y value,
                                                                         BiFunction<Expression<? extends Y>, Y, Predicate> function) {
      if (value != null) {
         predicates.add(function.apply(attributePath, value));
      }
   }

   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response create(TempData tempData) {
      DatabaseConnection.getInstance().withConnection(entityManager ->
            TempDataAdministrator.getInstance().create(entityManager, tempData.dropper(), tempData.item(), tempData.minimumQuantity(), tempData.maximumQuantity(), tempData.quest(), tempData.chance()));
      return Response.ok().entity(tempData).build();
   }

   @Path("/bulk")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createBulk(List<TempData> tempDatas) {
      DatabaseConnection.getInstance().withConnection(entityManager ->
            tempDatas.forEach(tempData ->
                  TempDataAdministrator.getInstance().create(entityManager, tempData.dropper(), tempData.item(), tempData.minimumQuantity(), tempData.maximumQuantity(), tempData.quest(), tempData.chance())));
      return Response.ok().entity(tempDatas).build();
   }

   @Path("{dropperId}/{itemId}")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response delete(@PathParam("dropperId") Integer dropperId, @PathParam("itemId") Integer itemId) {
      DatabaseConnection.getInstance().withConnection(entityManager -> TempDataAdministrator.getInstance().delete(entityManager, dropperId, itemId));
      return Response.ok().build();
   }
}
