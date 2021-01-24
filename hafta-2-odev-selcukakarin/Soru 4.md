# soru 4)
Product database'indeki public.content_category tablosunun dwh ortamına sample.content_category isminde her gün 1 defa kopyalandığını/extract edildiğini varsayalım.

sample.content_category tablosunda satışa çıkan productların id'leri ve category bilgileri yer almaktadır.​Tablodaki kolonların açıklamaları aşağıdaki gibidir.

cdc_date: İlgili kaydın oluşturulduğu ya da eğer güncellendi ise son güncellendiği timestamp değeri.

is_deleted: Kaydın silinip silinmediği bilgisi. Default değeri false'dur.

id: Product id'si. Primary key gibi düşünülebilir.

category: Product'ın ait olduğu kategori.​public.content_category tablosuna belirli aralıklarla delete-update-insert işlemleri uygulanmaktadır.

Bu durumda tabloya yeni kayıtlar eklenebilir, mevcut kayıtların kategori bilgisi güncellenebilir ya da kayıt silinebilir.
Mevcut bir kayıt güncellendiği durumda cdc_date alanı güncellenme tarihi ile değiştirilmektedir.

Yeni bir kayıt eklendiğinde de cdc_date alanı kaydın insert edildiği tarihi göstermektedir.

is_deleted alanının default değeri ise false'dur. public.content_category tablosunda silinen bir kayıt daha sonra tekrar insert edilmemektedir!
​Product database'indeki public.content_category tablosununun '2020-12-21 00:59' tarihinde sample.content_category tablosundaki kayıtları içerdiğini,

1 gün sonra '2020-12-22 00:59' tarihinde ise sample.content_category_20201222_00_59 tablosundaki kayıtları içerdiğini varsayalım.
​

Bizim isteğimiz sample.content_category ve sample.content_category_20201222_00_59 tablolarını karşılaştırarak
insert edilen yeni kayıtları sample.content_category tablosunada eklemek,
update edilen kayıtlar var ise sample.content_category tablosunda da update etmek ve
silinmiş olan kayıtların sample.content_category tablosundaki karşılıklarının is_deleted alanını true olarak güncellemek ve silmeden saklamaktır.
Ve bunu yaparken tek bir create or replace table ya da merge statementı kullanarak yapmak istiyoruz.
Gereken sorguyu çalıştırdıktan sonra sample.content_category ve sample.content_category_target tablolarının içerikleri birebir aynı olmalıdır!
​Aşağıdaki tabloları kendi datasetiniz altına kopyalamasınız!

sample.content_category > DATASET_ADINIZ.content_category

sample.content_category_20201222_00_59 > DATASET_ADINIZ.content_category_20201222_00_59

sample.content_category_target > DATASET_ADINIZ.content_category_target

​create or replace table statement: https://cloud.google.com/bigquery/docs/reference/standard-sql/data-definition-language#create_table_statement

​merge statement: https://cloud.google.com/bigquery/docs/using-dml-with-partitioned-tables#using_a_merge_statement

​2 tabloyu kıyaslamak için her satırın hash'ini alıp bu hash değerleri üzerinden joinleyip bir tabloda olup diğerinde olmayan hash değerleri var mıdır diye kontrol edebilirsiniz.

Hash fonksiyonunun örnek kullanımı aşağıdaki gibidir:

select farm_fingerprint(to_json_string(t1)) as _hash1

from `dsmbootcamp.sample.content_category_target` t1  

limit 100;  

```SQL
-- merge sistemi sadece target tablo üzerinde işlem yapmayı sağlıyor, source tablo üzerinde işlem yaptırmıyor.
MERGE `dsmbootcamp.selcuk_akarin.content_category` t    -- merge kısmına tablo vermek zorundayız.
USING `dsmbootcamp.selcuk_akarin.content_category_20201222_00_59` s    -- using'de kullanılan element sadece tablo olmak zorunda değil select de olabilir. yalnız on statement'ındaki alan unique olmalı.
ON t.id = s.id
WHEN NOT MATCHED BY SOURCE THEN
  UPDATE SET 
    t.is_deleted = true
WHEN MATCHED THEN
  UPDATE SET 
    t.cdc_date = s.cdc_date,
    t.is_deleted = s.is_deleted,
    t.id = s.id,
    t.category = s.category
WHEN NOT MATCHED BY TARGET THEN
  INSERT (
    cdc_date,
    is_deleted,
    id,
    category)
   VALUES (
    s.cdc_date,
    s.is_deleted,
    s.id,
    s.category)
    
```
