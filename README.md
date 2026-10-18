# AutoBoost (Minecraft 1.21.11 / Fabric, client-only)

## Derleme
1. https://fabricmc.net/develop adresinden 1.21.11 için güncel
   yarn / loader / fabric-api sürümlerini al ve gradle.properties'e yaz.
2. Resmi Fabric example mod şablonundan `gradlew`, `gradlew.bat` ve `gradle/` klasörünü bu klasöre kopyala.
3. JDK 21 ile:  ./gradlew build
4. build/libs/autoboost-1.1.0.jar -> .minecraft/mods (Fabric API ile birlikte)

## Özellikler (v1.1)
- Sınırsız FPS: Max Framerate'i oyun içinde "Sınırsız"a çeker (VSync'i elle KAPAT, yoksa monitör hızında kalır).
- Patlama koruması: TNT/kristal patlama paketi gelince 4 sn boyunca uzaktaki TNT/kristal/eşya/XP çizimi ve uzak patlama-duman parçacıkları atlanır.
- Otomatik mod: son 30 sn'nin en yüksek FPS'ine göre hedefi kendisi belirler, 4 kademeli kırpma.
- Ekranda "FPS 312 (+14/sn)" göstergesi.
- Ayar ekranı iki sütunlu, ana menüde Çoklu Oyuncu butonunun altında.

## Eski özellikler
- Her saniye FPS ölçer, hedef FPS altına düşünce uzaktaki eşya/XP çizimini atlar, düzelince kapatır.
- Pencere odakta değilken FPS sınırı (varsayılan 15).
- Bellek %92 üstüne çıkarsa arka planda temizlik (en fazla 2 dakikada bir).
- Grafik ayarlarına (render mesafesi, parçacık, gölge vb.) dokunmaz.
- Ana menüde "Çoklu Oyuncu" altında "AutoBoost Ayarları" butonu.
- Her şey try/catch içinde; hata olursa mod kendini kapatır, oyun çökmez.
- Ayar dosyası: config/autoboost.json

## GitHub ile otomatik derleme (kurulum gerektirmez)
1. github.com'da yeni bir repo aç.
2. Bu klasörün İÇİNDEKİ her şeyi (.github klasörü dahil) repoya yükle.
3. Repo > Actions sekmesi > "Build AutoBoost" bitince (3-5 dk) en alttaki "autoboost-jar" dosyasını indir.
4. Zip'in içindeki autoboost-1.1.0.jar dosyasını mods klasörüne at.
