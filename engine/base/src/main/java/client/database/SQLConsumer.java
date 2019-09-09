package client.database;

import java.sql.SQLException;

public interface SQLConsumer<T> {
   void accept(T t) throws SQLException;
}
