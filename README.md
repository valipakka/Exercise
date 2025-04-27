# Harjoitustyön Käyttöohjeet (Suomeksi)

Tässä ohjeet vaiheittain sovelluksen kloonaamisesta käynnistykseen ja peruskäyttöön.

## 1. Esivaatimukset

- **Java 21** on asennettuna (JDK 21).
- **Maven 3.8+** on asennettuna.
- (Vapaaehtoinen) **Docker & Docker Compose**, jos haluat ajaa kontissa.

## 2. Kloonaa ja siirry projektiin

1. Avaa komentorivi tai terminaali.
2. Suorita:
   ```bash
   git clone <repositoryn-URL>
   cd exercise
   ```

## 3. Rakenna ja käynnistä sovellus

1. Projekin juuressa (hakemistossa, jossa on `pom.xml`) aja:
   ```bash
   mvn clean spring-boot:run
   ```
2. Odota, että Maven lataa riippuvuudet ja käynnistää sovelluksen.
3. Kun konsolissa näkyy rivi:
   ```
   Started Application in ... seconds
   🔐 Admin user created: admin@example.com
   ```
   sovellus on käynnissä.

## 4. Avaa selain

- Siirry osoitteeseen:
  ```
  http://localhost:8080
  ```
- Näet etusivun (Home).

## 5. Kirjautuminen ja rekisteröityminen

YLÄ REUNASSA ON "TOGGLE THEME" NAPPI. SILLÄ SAA VAIHDETTUA TAUSTAVÄRIN. EN SAANUT FONTTIA VAIHTUMAAN.

1. Päävalikosta (header) klikkaa **Login**, jos et ole vielä kirjautuneena.
2. Olemassa olevat käyttäjät:
   - **Admin**
     - Sähköposti: `admin@example.com`
     - Salasana: `admin`
   - **User**
     - Luo oma käyttäjä **Sign up** -napista.
3. Kirjautumisen jälkeen headeriin ilmestyy **My Exercises** ja **Log out**.

## 6. Harjoitusten hallinta (My Exercises)

1. Klikkaa **My Exercises**.
2. Näet listan omista harjoituksistasi.
3. Voit suodattaa rivejä:
   - Tyyppi (Type)
   - Etäisyys (Distance)
   - Muistiinpanot (Notes)
   - Aloitusaika (Start Time)
4. Lisätäksesi uuden harjoituksen klikkaa **Add New**, täytä tiedot ja paina **Save**.
5. Muokkaa tai poista harjoituksia klikkaamalla rivin tietoja.

## 7. Admin-näkymä

- Jos olet **Admin**, headerissä näkyy myös **Admin**.
- Klikkaamalla Admin pääset hallinnoimaan käyttäjiä.

## 8. Uloskirjautuminen

- Klikkaa **Log out** headerissa.
- Sovellus ohjaa takaisin etusivulle ja header näyttää taas **Login**.

## 9. H2-konsoli

1. Avaa selaimessa:
   ```
   http://localhost:8080/h2-console
   ```
2. JDBC URL on `jdbc:h2:mem:…` (automaattinen). Kirjaudu käyttäjällä `SA` ilman salasanaa.

## 10. Itsearviointi
En valitettavasti antanut tälle kurssille tarpeeksi aikaa, jotta olisin voinut panostaa tähän kunnolla. Jouduin käyttämään GitHubin copilottia paljon, että sain tehtävän tehtyä, koska en kerennyt osallistua tunneille ja tallenteiden katsominen meni ajatukset muualla. Luulen onnistuneeni täyttämään arvostelukategorioissa seuraavasti
1. 3 pistettä. Minulla on myös muokkaus ja poisto mutta 4 kohta jäi suorittamatta.
2. 4 pistettä. Viides ehto oli jo olemassa, mutta ajan puutteen ja sen tuomien ongelmien takia jouduin poistamaan sen.
3. En ole varma mitä tästä saan. Tämä jäi tausta ajatukseksia jonka muistin vasta kun olin jo palauttamassa tehtävää.
4. 5 pistettä. Tämän sain mielestäni täysin valmiiksi.
5. 4 pistettä. Tämä oli alue joka tuotti minulle suurimmat vaikeudet. En ole vieläkään varma miten sain tämän loppujen lopuksi toimimaan.
6. 2 pistettä. Git ja salasanan salaus. Jälkimmäisestä en ole varma, mutta luulen onnistuneeni siinä.

