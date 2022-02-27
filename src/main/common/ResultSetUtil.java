package main.common;

import main.domain.Account;
import main.domain.Transaction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.UUID;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

public class ResultSetUtil {

   public static Account accountFromResultSet(ResultSet rs) throws SQLException {
       final UUID key = UUID.fromString(rs.getString("key"));
       final String name = rs.getString("name");
       final int balance = rs.getInt("balance");
       final ZonedDateTime timestamp = ZonedDateTime.parse(rs.getString("timestamp"), ISO_OFFSET_DATE_TIME);

       return new Account(key, name, balance, timestamp.toLocalDateTime());
   }

   public static Transaction transactionFromResultSet(ResultSet rs) throws SQLException {
       final UUID key = UUID.fromString(rs.getString("accountkey"));
       final String description = rs.getString("description");
       final int amount = rs.getInt("amount");
       final ZonedDateTime timestamp = ZonedDateTime.parse(rs.getString("timestamp"), ISO_OFFSET_DATE_TIME);

       return new Transaction(timestamp.toLocalDateTime(), key, description, amount);
   }
}
