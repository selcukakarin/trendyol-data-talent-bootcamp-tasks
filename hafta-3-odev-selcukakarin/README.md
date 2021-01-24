# hafta-3

## Ödevi almak için lütfen linke tıklayın.

link: https://classroom.github.com/a/FmdVa8il

### Soru 1

`dsmbootcamp.sample.semi_structured_hw` tablosunda 2 kişinin sahip olduğu araç bilgileri ve bu araçları satın alma tarihleri yer almaktadır.
Tablodaki kolonların açıklamaları aşağıdaki gibidir.
​
- name<String>: Araç sahibinin adı
- car<Repeated Record>: Araç id'si ve model bilgilerini içermektedir.
- manufacture<Repeated Record>: Araç id'si, üretim yılı ve üretildiği ülke bilgilerini içermektedir.
- purchase<Repeated Record>: Araç id'si ve satın alma tarihlerini içermektedir. 

​
Beklenen `dsmbootcamp.sample.semi_structured_hw` tablosunu kullanarak `dsmbootcamp.sample.semi_structured_hw_expected` tablosundaki 
veriyi oluşturmaktır. 

​
`semi_structured_hw_expected` tablosundaki veriyi oluşturmak için yapılması gerekenler:
- name kolonunu seçmek
- car ve manufacture arraylerini unnest ederek her iki array içindeki id kolonları üzerinden joinlemek
- Unnest ettiğimiz bu 2 array içinden car.id, car.model ve manufacture.year alanlarını uygun aliaslar ile seçmek
- purchase array'i içindeki date kolonundaki her değere 3 saat eklemek ve purchase kolonunu tekrar array olarak tutmak
​
*3.hafta Semi-Structured Queries dersinde içinde struct olan bir arrayin mevcut kolonunda nasıl değişiklik yapabileceğimizi görmüştük.*
*Örnek olması için aşağıdaki sorguarı inceleyebilirsiniz.*
​
```sql
select *, array(select as struct m.* replace((m.year + 1) as year) from unnest(manufacture) as m) as manufacture_modified  
from `dsmbootcamp.sample.array_struct_car`;  
​
select *, array(select as struct m.id, (m.year + 1) as year, country from unnest(manufacture) as m) as manufacture_modified  
from `dsmbootcamp.sample.array_struct_car`;
```





