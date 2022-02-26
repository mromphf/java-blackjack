
SET search_path TO 'blackjack';

SELECT c.timestamp, a.name
  FROM accounts a
  JOIN account_closures c
    ON a.key = c.key
ORDER BY c.timestamp DESC;
