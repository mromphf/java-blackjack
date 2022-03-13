
SET search_path TO 'blackjack';

DROP VIEW IF EXISTS blackjack.account_balances;
DROP VIEW IF EXISTS blackjack.closed_accounts;
DROP VIEW IF EXISTS blackjack.account_stats;
DROP VIEW IF EXISTS blackjack.active_transactions;


CREATE VIEW blackjack.active_transactions AS
     SELECT accountkey, description, amount, TO_CHAR(t.timestamp AT TIME ZONE 'UTC', 'YYYY-MM-DDThh:mm:SS-06:00') AS timestamp
      FROM transactions t
      JOIN accounts a
        ON t.accountKey = a.key
    WHERE a.key NOT IN (SELECT key FROM account_closures);


CREATE VIEW blackjack.closed_accounts AS
    SELECT c.timestamp "Date Closed", a.key, a.name, COUNT(t.*) AS "transactions"
      FROM accounts a
      JOIN account_closures c
        ON c.key = a.key
      JOIN transactions t
        ON t.accountkey = a.key
  GROUP BY "Date Closed", a.key, a.name;


CREATE VIEW blackjack.account_stats AS
    SELECT a.key, TO_CHAR(a.timestamp AT TIME ZONE 'UTC', 'YYYY-MM-DDThh:mm:SS-06:00') AS timestamp, a.name,
        SUM(t.amount) AS "balance",
        COUNT(t.accountkey) AS "transactions",

        (SELECT TRUNC(AVG(ABS(ts.amount)), 0)
           FROM transactions ts
          WHERE ts.accountkey = a.key
           AND ts.description = 'BET'
          GROUP BY ts.accountkey) AS "avg. bet",

        (SELECT COUNT(ts.accountkey)
           FROM transactions ts
          WHERE ts.accountkey = a.key
           AND ts.description = 'BLACKJACK'
          GROUP BY ts.accountkey) AS "blackjacks",

        (SELECT COUNT(ts.accountkey)
           FROM transactions ts
          WHERE ts.accountkey = a.key
           AND ts.description = 'WIN'
          GROUP BY ts.accountkey) AS "wins",

        (SELECT COUNT(ts.accountkey)
           FROM transactions ts
          WHERE ts.accountkey = a.key
           AND ts.description = 'LOSE'
          GROUP BY ts.accountkey) AS "losses",

        (SELECT COUNT(ts.accountkey)
           FROM transactions ts
          WHERE ts.accountkey = a.key
           AND ts.description = 'BUST'
          GROUP BY ts.accountkey) AS "busts",

        (SELECT COUNT(ts.accountkey)
           FROM transactions ts
          WHERE ts.accountkey = a.key
           AND ts.description = 'PUSH'
          GROUP BY ts.accountkey) AS "pushes"

      FROM accounts a
      JOIN transactions t
        ON a.key = t.accountkey
     WHERE a.key NOT IN (SELECT key FROM account_closures)
  GROUP BY a.key, a.name, a.timestamp
  ORDER BY a.name, "balance", "transactions";




