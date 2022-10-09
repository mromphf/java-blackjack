DROP VIEW account_balances;

CREATE VIEW account_balances AS
SELECT a.key, a.timestamp, a.name,
       SUM(t.amount) AS balance,
       COUNT(t.accountKey) AS transactions
FROM accounts a
         JOIN transactions t
              ON a.key = t.accountkey
WHERE a.key NOT IN (SELECT key FROM account_closures)
        GROUP BY a.key, a.name, a.timestamp
        ORDER BY balance DESC;
