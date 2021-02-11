# Soru 1) 1980’den itibaren spor grubu bazında en çok madalya alan 1. 3. 5. ülkeyi bulalım.

```SQL
WITH medal_winners AS
(
  SELECT 
    Sport,
    Country, 
    count(1) as medal_count
  FROM `dsmbootcamp.selcuk_akarin.summer_medals` 
  WHERE medal IS NOT NULL AND year >= 1980 
  GROUP BY Sport,Country
  ORDER BY Sport,medal_count DESC
)

SELECT DISTINCT Sport,
  first_value(Country) OVER(PARTITION BY Sport ORDER BY medal_count DESC range BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS first_champ_country,
  nth_value(Country,3) OVER(PARTITION BY Sport ORDER BY medal_count DESC range BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS third_champ_country,
  nth_value(Country,5) OVER(PARTITION BY Sport ORDER BY medal_count DESC range BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS fifth_champ_country,
FROM medal_winners
```
