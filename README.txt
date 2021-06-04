Az applikáció indításához legelőször a Server runconfiguration-t kell kiválasztani és azt elindítani.
Ez elindítja a server-t ami ezután várja a beérkező klienseket. A klienst a Main runconfiguration-nel
lehet elindítani, egyszerre akár többet is (multiple instances allowed).

Ezt követően a kliens elíndítása után a "MainPage" fogadja a felhasználót (egy fxml segítségével
megalkotott oldal). Itt lehetséges a regisztráció és a bejelentkezés is. Regisztrációnál String
objektumokban tárolom először a felhasználói adatokat, ezért ennek megfelelően sokféle karaktert lehet
megadni (alapvetően nem alkalmaztam formai ellenőrzést még az email formátumra). A regisztrációt követően 
a kliens elküldi a felhasználói adatokat a server-nek, ami pedig egy sqlite adatbázisban
(src/Server/chat.db) tárolja ezeket. Egy felhasználó nevet csak egyszer lehet regisztálni, mivel az
adatbázisban a username lett beállítva primary key-nek. A jelszavakat egyelőre nem hash formátumban
tárolja az adatbázis, hanem sima text-ként.

A regisztrációt követően lehetséges a bejelentkezés. Ezt követően lehetséges egy másik klienssel való
kommunikáció lefolytatása (amint párhuzamosan elindítunk egy másik klienst a Main runconfiguration-nal)

A bal oldalon található ListView tartalmazza a regisztrált felhasználókat. A lista frissített verzióját
minden egyes regisztráció után megkapja a kliens, viszont ezt külön manuálisan kell frissíteni a Refr.Users
gombbal. A kommunikációs partner kiválasztása után megjelennek a partnerrel folytatott előző beszélgetések
is, mivel ezeket is eltárolja az adatbázis egy-egy külön táblában, a user nevével a tábla nevének való
beállításával. Tehát maga a user táblája a minden hozzá köthető kommunikációt tárol, valamint az innen
a record-ok kivétele ezért természetesen időrendben történik, ezeket pedig Arraylist-ekben tárolják
a kliensek, emiatt a sorrenddel külön nem kellett foglalkozni, hogy időrendbe legyenek mert így is
abban lesznek.

A Send gombbal lehetséges az üzenet küldése. A küldőnél ez alapvetően egyből meg is jelenik, viszont a
másik kliens (ugyan egyből megkapja: látható a másik kliens standard outputján a beérkező üzenet) esetén
az üzeneteket csak úgy lehet frissíteni, ha a felhasználó egy másik user-t kiválaszt a bal oldali ListView-ból,
majd visszatér az előző kommunikációs partnerhez.

Kijelentkezni a Logout gombbal lehetséges. Ekkor lezárulnak a Socket-ek, viszont a MainPage ablakon
az "X"-re kattintva logout nélkül a socket-ek lezárása nélkül is lehetséges a kliens programot lezárni.
Ekkor Exception dobódik a Server-nél a ClientHandler osztályban, és ezt a "User exit without logout!"
üzenet standard output-ra való kiíratásával jelzi a server.

Szálkezelés:

A kliens minden üzenetet egyből olvas, mivel külön szálon fut az írás és olvasás: írás a Main osztályban 
valósul meg, azzal, hogy a RegistrationPage, a LoginPage vagy a MainPage megnyitásával példányosítódik a 
RegistrationController, a LoginController vagy a MainController, és mindegyik előző osztály meghívja 
valamely metódusában a sendMessage() függvényt. Az olvasás pedig a ReaderThread osztályban valósul meg,
ezt a Client osztályban példányosítom az ő socket-jének az inputStream-jének a konstruktornak való átadásával.

Továbbá természetesen a server az ExecutorService segítségével minden egyes kliens-t külön szálon kezel
egy-egy ClientHandler példány létrehozásával a megfelelő clienSocket átadásával a konstruktorban. Ezen
kívül ezeket a ClientHandler objektumokat a clientsWithUsername HashMap-ben tárolom el minden egyes sikeres
login után, és így tudnak direkt üzeneteket egymásnak küldeni a user-ek.

Egyéb:

A kliens program lezárásakor egy properties állományba menti ki a legutolsó ablak-pozíciót a kliens, amit
a következő induláskor felhasználva ugyanoda helyezi el az ablakot a program.