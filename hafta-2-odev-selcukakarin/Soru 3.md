# Soru 3) "sample.pageview": tablosunda 1 gün içerisinde trendyol.com a gelen tüm ziyaretlerin logu var.
--view_ts: ziyaret zamanı

--channel: android,ios,web.

--pagetype: görüntülenen sayfa: homepage, order, boutiquedetail, productdetail.. gibi.

--deviceid: sayfa ziyareti yapan cihaz id'si. Bizim için tekil kullanıcıyı da ifade eder.

tabloya veri akışı dakikada 1 defa gerçekleşir, 23:10:00 anında 23:09:00-23:09:59 kayıtları tabloya eklenir.

Bu çalışmada çıkarmak istediğimiz bilgi, günün her bir dakikası için aktif kullanıcı sayısının hesaplanması.
Aktif kullanıcı ne demek?

sitede herhangi bir sayfa ziyareti sonrasında 5dk boyunca aktif kullanıcı sayılır.

bir örnek ile: "2020-03-03 23:10:14" anınında 100farklı cihaz trendyol'u açıp kapattı ise ve sonrasında hiç bir ziyaret gelmedi ise.

--"2020-03-03 23:10" 100 aktif kullanıcı vardır.

--"2020-03-03 23:11" 100 aktif kullanıcı vardır.

--"2020-03-03 23:12" 100 aktif kullanıcı vardır.

--"2020-03-03 23:13" 100 aktif kullanıcı vardır.

--"2020-03-03 23:14" 100 aktif kullanıcı vardır.

--"2020-03-03 23:15" 0 aktif kullanıcı vardır.

en basit çözüm ile sadece "2020-03-03 23:14" 'deki aktif kullanıcıları hesaplamak için:
--select timestamp '2020-03-03 23:14:00' view_period

-- ,count(distinct deviceid) active_user_count

-- from sample.pageview

--where timestamp_trunc(view_ts,minute) between '2020-03-03 23:10:00' and '2020-03-03 23:14:00'

Yazacağınız sorgu/sorguların çıktısında beklediğimiz çıktı sitedeki dakikalık aktif kullanıcı sayısı:
-- view_period active_user_count

-- 2020-03-03 23:14:00 123123123

-- 2020-03-03 23:13:00 125123127

-- 2020-03-03 23:12:00 126123124

NOT: Çözümde göz önünde bulundurmanızı istediğimiz koşullar:
exact sonuç aramıyoruz, %2'ye kadar sapmalar kabul edilebilir, approx fonksiyonları kullanabilirsiniz

örnek tabloda 289m kayıt var, bu aylar öncesinin verisi, optimizasyonlar için summary, ara tablo oluşturabilirsiniz.

tek sorguda hesaplayabilirsiniz, temporary tablolar oluşturup, 1den fazla sorgu ile de çözebilirsiniz.

hll_count.init, hll_count.merge, hll_count.merge_partial, hll_count.extract kullanabilirsiniz.

https://cloud.google.com/bigquery/docs/reference/standard-sql/hll_functions

[2. versiyon Faruk Aşçı'nın kodu örnek alınarak tasarlanmıştır.](https://github.com/Trendyol-Data-Talent-Bootcamp/hafta-2-odev-FarukAsci/blob/main/Soru%203.md)

```SQL
-- 1. versiyon
SELECT time,
SUM(device_count) OVER(ORDER BY time ASC ROWS BETWEEN 4 PRECEDING and CURRENT ROW) AS distinct_user
FROM(
SELECT DISTINCT FORMAT_TIME("%R", EXTRACT(TIME FROM view_ts)) AS time ,
COUNT(deviceid) AS device_count
FROM selcuk_akarin.pageview 
GROUP BY time
ORDER BY time ASC
)
-- 2. versiyon
with hyper_count as(
SELECT * FROM(
SELECT FORMAT_TIME("%R", EXTRACT(TIME FROM view_ts)) AS time_min, HLL_COUNT.init(deviceid) users
FROM selcuk_akarin.pageview
GROUP BY time_min)
ORDER BY time_min), 

five_minute_window AS (
  SELECT
    time_min,
    ARRAY_AGG(users) OVER (ROWS BETWEEN 4 PRECEDING AND CURRENT ROW) five_minute_users
  FROM hyper_count)
  
SELECT
  time_min,(
  SELECT
    HLL_COUNT.merge(users)
  FROM
    UNNEST(five_minute_users) users) user_count
FROM
  five_minute_window
ORDER BY
  time_min;
  
-- lets try  
SELECT count(distinct(deviceid))
FROM selcuk_akarin.pageview
where timestamp_trunc(view_ts,minute) between '2020-03-03 23:55:00 UTC' and '2020-03-03 23:59:00 UTC'
```
