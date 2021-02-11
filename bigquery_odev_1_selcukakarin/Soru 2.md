# Soru 2) 1980’den itibaren herhangi bir spor grubunda üst üste 3 veya daha fazla madalya almış atletleri bulalım.
Aşağıdaki şekilde

create or replace table dsmbootcamp.DATASET_ADINIZ.summer_medals

as

select * from dsmbootcamp.sample.summer_medals;

DATASET_ADINIZ kısmına kendi dataset adınızı yazarak tabloyu create ettikten sonra, soru çözümünüzde bu yarattığınız tabloyu kullanabilirsiniz.

```SQL

WITH medal_winners AS
(
  SELECT
    year,
    athlete,
    country,
    sport,
    medal,
    COUNT(1) OVER(PARTITION BY athlete ORDER BY year ROWS BETWEEN 1 PRECEDING AND 1 FOLLOWING) AS medal_counter,
    NTH_VALUE (year,1) OVER(PARTITION BY athlete ORDER BY year ROWS BETWEEN  1 PRECEDING AND 1 FOLLOWING) AS first_,
    NTH_VALUE (year,2) OVER(PARTITION BY athlete ORDER BY year DESC ROWS BETWEEN 1 PRECEDING and 1 FOLLOWING) AS second_,
    NTH_VALUE (year,3) OVER(PARTITION BY athlete ORDER BY year ROWS BETWEEN  1 PRECEDING AND 1 FOLLOWING) AS last_,
  FROM `dsmbootcamp.selcuk_akarin.summer_medals`
  WHERE medal IS NOT NULL AND year >= 1980 
  ORDER BY year DESC
)

SELECT
    year,
    athlete,
    country,
    sport,
    first_,
    second_,
    last_
FROM medal_winners
WHERE last_ is not null and first_ is not null and second_ is not null and last_-first_=8 and last_-second_=4 and medal_counter = 3;
```
