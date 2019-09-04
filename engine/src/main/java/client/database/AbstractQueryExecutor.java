package client.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractQueryExecutor {
   protected <T> Optional<T> getSingle(Connection connection, String sql, int columnIndex) {
      return getSingle(connection, sql, null, columnIndex);
   }

   protected <T> Optional<T> getSingle(Connection connection, String sql, SQLConsumer<PreparedStatement> setParams, int columnIndex) {
      return get(connection, sql, setParams, rs -> {
         if (rs != null && rs.next()) {
            return Optional.ofNullable((T) rs.getObject(columnIndex));
         }
         return Optional.empty();
      });
   }

   protected <T> Optional<T> getSingle(Connection connection, String sql, String columnLabel) {
      return getSingle(connection, sql, null, columnLabel);
   }

   protected <T> Optional<T> getSingle(Connection connection, String sql, SQLConsumer<PreparedStatement> setParams, String columnLabel) {
      return get(connection, sql, setParams, rs -> {
         if (rs != null && rs.next()) {
            return Optional.ofNullable((T) rs.getObject(columnLabel));
         }
         return Optional.empty();
      });
   }

   protected void execute(Connection connection, String sql, SQLConsumer<PreparedStatement> setParams) {
      try (PreparedStatement ps = connection.prepareStatement(sql)) {
         setParams.accept(ps);
         ps.executeUpdate();
      } catch (SQLException exception) {
         exception.printStackTrace();
      }
   }

   protected void executeNoParam(Connection connection, String sql) {
      try (PreparedStatement ps = connection.prepareStatement(sql)) {
         ps.executeUpdate();
      } catch (SQLException exception) {
         exception.printStackTrace();
      }
   }

   protected int insertAndReturnKey(Connection connection, String sql, SQLConsumer<PreparedStatement> setParams) {
      int id = -1;
      try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
         setParams.accept(ps);
         ps.executeUpdate();
         try (ResultSet rs = ps.getGeneratedKeys()) {
            rs.next();
            id = rs.getInt(1);
         }
      } catch (SQLException exception) {
         exception.printStackTrace();
      }
      return id;
   }

   protected <T> void batch(Connection connection, String sql, SQLBiConsumer<PreparedStatement, T> setParams, Iterable<T> dataPoints) {
      try (PreparedStatement ps = connection.prepareStatement(sql)) {
         for (T dataPoint : dataPoints) {
            setParams.accept(ps, dataPoint);
            ps.addBatch();
         }
         ps.executeBatch();
      } catch (SQLException exception) {
         exception.printStackTrace();
      }
   }

   protected <T> Optional<T> getNew(Connection connection, String sql, SQLConsumer<PreparedStatement> setParams, SQLFunction<ResultSet, T> getResult) {
      return get(connection, sql, setParams, rs -> {
         if (rs != null && rs.next()) {
            return Optional.of(getResult.apply(rs));
         }
         return Optional.empty();
      });
   }

   protected <T> Optional<T> getNew(Connection connection, String sql, SQLFunction<ResultSet, T> getResult) {
      return getNew(connection, sql, null, getResult);
   }

   @Deprecated
   protected <T> Optional<T> get(Connection connection, String sql, SQLConsumer<PreparedStatement> setParams, SQLFunction<ResultSet, Optional<T>> getResult) {
      return getInternal(connection, sql, setParams, getResult, Optional.empty());
   }

   @Deprecated
   protected <T> Optional<T> get(Connection connection, String sql, SQLFunction<ResultSet, Optional<T>> getResult) {
      return get(connection, sql, null, getResult);
   }

   @Deprecated
   protected <T> List<T> getList(Connection connection, String sql, SQLConsumer<PreparedStatement> setParams, SQLFunction<ResultSet, List<T>> getResult) {
      return getInternal(connection, sql, setParams, getResult, new ArrayList<>());
   }

   @Deprecated
   protected <T> List<T> getList(Connection connection, String sql, SQLFunction<ResultSet, List<T>> getResult) {
      return getList(connection, sql, null, getResult);
   }

   protected <T> List<T> getListNew(Connection connection, String sql, SQLConsumer<PreparedStatement> setParams, SQLFunction<ResultSet, T> getResult) {
      return getList(connection, sql, setParams, rs -> {
         List<T> result = new ArrayList<>();
         while (rs != null && rs.next()) {
            result.add(getResult.apply(rs));
         }
         return result;
      });
   }

   protected <T> List<T> getListNew(Connection connection, String sql, SQLFunction<ResultSet, T> getResult) {
      return getListNew(connection, sql, null, getResult);
   }

   private <T> T getInternal(Connection connection, String sql, SQLConsumer<PreparedStatement> setParams, SQLFunction<ResultSet, T> getResult, T default_) {
      T result = default_;
      try (PreparedStatement ps = connection.prepareStatement(sql)) {
         if (setParams != null) {
            setParams.accept(ps);
         }
         ResultSet rs = ps.executeQuery();
         result = getResult.apply(rs);
      } catch (SQLException exception) {
         exception.printStackTrace();
      }
      return result;
   }
}
