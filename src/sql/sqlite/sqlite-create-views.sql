DROP VIEW IF EXISTS account_balances;
DROP VIEW IF EXISTS account_balances_inc_closed;
DROP VIEW IF EXISTS active_transactions;


CREATE VIEW active_transactions AS
SELECT accountkey, description, amount, t.timestamp
FROM transactions t
         JOIN accounts a
              ON t.accountKey = a.key
WHERE a.key NOT IN (SELECT key FROM account_closures);


CREATE VIEW account_balances AS
SELECT a.key,
       a.timestamp,
       a.name,
       SUM(t.amount)       AS balance,
       COUNT(t.accountKey) AS transactions
FROM accounts a
         JOIN transactions t
              ON a.key = t.accountkey
WHERE a.key NOT IN (SELECT key FROM account_closures)
GROUP BY a.key, a.name, a.timestamp
ORDER BY balance DESC;


CREATE VIEW account_balances_inc_closed AS
SELECT a.key,
       a.timestamp,
       a.name,
       SUM(t.amount)       AS balance,
       COUNT(t.accountKey) AS transactions
FROM accounts a
         JOIN transactions t
              ON a.key = t.accountkey
WHERE a.key NOT IN (SELECT key FROM account_closures)
GROUP BY a.key, a.name, a.timestamp
ORDER BY balance DESC;


CREATE VIEW IF NOT EXISTS cards_drawn AS
SELECT pc.timestamp, a.name, rnk.name, c.suit
FROM player_cards pc
         JOIN cards c on pc.cardKey = c.cardKey
         JOIN ranks rnk on c.rank = rnk.value
         JOIN rounds r on pc.roundKey = r.key
         JOIN accounts a on pc.accountKey = a.key
ORDER BY pc.timestamp DESC;


CREATE VIEW actions_taken AS
SELECT instance.timestamp, account.name, instance.actionName
FROM action_instances instance
         JOIN accounts account on instance.accountKey = account.key
ORDER BY instance.timestamp DESC;