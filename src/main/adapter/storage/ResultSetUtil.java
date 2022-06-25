package main.adapter.storage;

import main.domain.Account;
import main.domain.Transaction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.UUID;

import static java.time.ZonedDateTime.parse;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static main.domain.Transaction.transaction;

public class ResultSetUtil {

   public static Account accountFromResultSet(ResultSet rs) throws SQLException {
       final UUID key = UUID.fromString(rs.getString("key"));
       final String name = rs.getString("name");
       final int balance = rs.getInt("balance");
       final ZonedDateTime timestamp = parse(rs.getString("timestamp"), ISO_OFFSET_DATE_TIME);

       return new Account(key, name, balance, timestamp.toLocalDateTime());
   }

   public static Transaction transactionFromResultSet(ResultSet rs) throws SQLException {
       return transaction(
               parse(rs.getString("timestamp"), ISO_OFFSET_DATE_TIME).toLocalDateTime(),
               UUID.fromString(rs.getString("accountkey")),
               rs.getString("description"),
               rs.getInt("amount"));
   }
}
