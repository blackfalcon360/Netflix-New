# Netflix New

Tells you about new movies and TV shows on Netflix, filtered by **genres** and by **keywords**.
Java, no AndroidX, no libraries. Package: `com.blackfalcon.netflixnew`. By: Black Falcon

## How it works
Netflix has no public list of new titles. The app asks **TMDB** (free API) for movies and TV shows that are on Netflix (flat-rate) in your
country and that were released, or have new episodes, in the last N days, then filters them by your rules.
- **Rules**: each rule has a name, type (movies / TV / both), genres (any of the chosen ones, or all of them), keywords and a look-back window.
  Keywords use TMDB's keyword database (accurate, English); if a word is not found, or you choose "words in title or overview", the app
  searches the title and overview text instead.
- **New tab**: results grouped by rule with posters, a NEW badge for titles that were not there in the previous check, a "New episodes" badge
  for shows that started earlier, and buttons to open the title in Netflix (search) or on TMDB.
- **Automatic checks** every 6 / 12 / 24 hours (or off) with a notification when new titles match; the first check of a rule only builds the list.

## Setup (needed once)
1. Make a free account at themoviedb.org, open Settings > API and request an API key (v3). 
2. Paste it in this app's Settings tab, tap **Test key**, choose your country (for example PK) and **Save**.

## Build
Upload the folder contents to GitHub; the "Build APK" workflow gives `NetflixNew-APK` under Artifacts.

## Privacy
Internet permission is used only to talk to `api.themoviedb.org` (your TMDB key, country, genre/keyword/date filters) and `image.tmdb.org`
(posters). Nothing else is sent; the app does not see your Netflix account. Notifications need permission on Android 13+.

## Honest limits
- Availability data comes from TMDB (JustWatch) and can be late or incomplete; catalogs differ by country.
- "New" means a recent release or new episodes, not the exact date Netflix added the title (TMDB does not provide that).
- Automatic checks use Android's job scheduler (not a permanent foreground service, so there is no always-on notification). Android may delay
  them. On Xiaomi / Oppo / Vivo / Huawei phones give this app **Autostart** and **no battery restriction**.
- Not compiled or tested on a device by the author. The request building, answer parsing, rule logic and new-title tracking were tested on a
  computer with fake TMDB answers, but the real network calls were not run.

This product uses the TMDB API but is not endorsed or certified by TMDB. Streaming availability data is provided by JustWatch.
Netflix is a trademark of Netflix, Inc.; this app is not affiliated with Netflix.

## اردو (مختصر)
- یہ ایپ TMDB سے وہ فلمیں اور سیریز لیتی ہے جو آپ کے ملک میں نیٹ فلکس پر ہیں اور حال ہی میں آئی ہیں، پھر آپ کی صنفوں اور کی ورڈز کے مطابق چھانتی ہے۔
- ایک بار مفت TMDB API کی بنائیں (themoviedb.org)، سیٹنگز میں ڈالیں، "Test key" دبائیں۔
- نیٹ فلکس نئے ٹائٹلز کی کوئی عوامی فہرست نہیں دیتا، اس لیے "نیا" کا مطلب حالیہ ریلیز یا نئی اقساط ہے، شامل ہونے کی اصل تاریخ نہیں۔
- خودکار چیک جاب شیڈیولر سے ہوتے ہیں؛ ژیاؤمی/اوپو/ویوو/ہواوے پر Autostart اور بیٹری پابندی ہٹائیں۔
