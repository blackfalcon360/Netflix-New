package com.blackfalcon.netflixnew;

import android.content.Context;
import java.util.HashMap;

/** All UI text in English (0), Urdu (1), Arabic (2). */
final class S {
    private S() {}

    private static final HashMap<String, String[]> M = new HashMap<String, String[]>();

    private static void a(String k, String en, String ur, String ar) {
        M.put(k, new String[]{en, ur, ar});
    }

    static {
        a("app_name", "Netflix New", "نیٹ فلکس نیو", "نتفليكس الجديد");
        a("tab_new", "New", "نیا", "الجديد");
        a("tab_rules", "Rules", "اصول", "القواعد");
        a("tab_settings", "Settings", "سیٹنگز", "الإعدادات");
        a("by", "By: Black Falcon \uD83E\uDD85", "By: Black Falcon \uD83E\uDD85", "By: Black Falcon \uD83E\uDD85");
        a("back", "Back", "واپس", "رجوع");
        a("yes", "Yes", "ہاں", "نعم");
        a("no", "No", "نہیں", "لا");
        a("close", "Close", "بند کریں", "إغلاق");
        a("exit_title", "Exit", "باہر نکلیں", "خروج");
        a("exit_msg", "Do you want to exit the app?", "کیا آپ ایپ سے باہر نکلنا چاہتے ہیں؟", "هل تريد الخروج من التطبيق؟");
        a("error", "Something went wrong.", "کچھ غلط ہو گیا۔", "حدث خطأ ما.");
        // new tab
        a("n_check", "Check now", "ابھی چیک کریں", "افحص الآن");
        a("n_checking", "Checking Netflix titles...", "نیٹ فلکس کے ٹائٹلز چیک ہو رہے ہیں...", "جارٍ فحص عناوين نتفليكس...");
        a("n_last", "Last check: %s", "آخری چیک: %s", "آخر فحص: %s");
        a("n_never", "Not checked yet.", "ابھی چیک نہیں ہوا۔", "لم يتم الفحص بعد.");
        a("n_empty", "Nothing found for your rules in this period.", "اس مدت میں آپ کے اصولوں کے مطابق کچھ نہیں ملا۔", "لا نتائج لقواعدك في هذه الفترة.");
        a("n_new", "NEW", "نیا", "جديد");
        a("n_eps", "New episodes", "نئی اقساط", "حلقات جديدة");
        a("n_movie", "Movie", "فلم", "فيلم");
        a("n_tv", "TV show", "سیریز", "مسلسل");
        a("n_netflix", "Netflix", "نیٹ فلکس", "نتفليكس");
        a("n_tmdb", "TMDB", "TMDB", "TMDB");
        a("n_seen", "Mark all as seen", "سب کو دیکھا ہوا نشان زد کریں", "تحديد الكل كمشاهَد");
        a("n_baseline", "The first check of a rule only lists what is there. Later checks mark and notify new titles.", "کسی اصول کا پہلا چیک صرف موجودہ فہرست دکھاتا ہے۔ بعد کے چیک نئے ٹائٹلز نشان زد کرتے اور اطلاع دیتے ہیں۔", "أول فحص للقاعدة يعرض الموجود فقط. الفحوص اللاحقة تحدد العناوين الجديدة وتنبّه إليها.");
        a("n_setup_title", "Setup needed", "سیٹ اپ چاہیے", "يلزم الإعداد");
        a("n_setup", "To learn what is on Netflix, this app needs a free TMDB API key:\n1) Make a free account at themoviedb.org\n2) Open Settings > API and request an API key (v3)\n3) Paste the key in this app's Settings tab.", "نیٹ فلکس پر کیا ہے یہ جاننے کے لیے ایپ کو مفت TMDB API کی چاہیے:\n1) themoviedb.org پر مفت اکاؤنٹ بنائیں\n2) Settings > API کھول کر API key (v3) مانگیں\n3) کی کو اس ایپ کے سیٹنگز ٹیب میں پیسٹ کریں۔", "لمعرفة ما في نتفليكس يحتاج التطبيق إلى مفتاح TMDB API مجاني:\n1) أنشئ حسابًا مجانيًا في themoviedb.org\n2) افتح Settings > API واطلب مفتاح API (v3)\n3) الصق المفتاح في تبويب الإعدادات هنا.");
        a("n_go", "Open Settings", "سیٹنگز کھولیں", "افتح الإعدادات");
        a("e_key", "TMDB rejected the API key. Check it in Settings.", "TMDB نے API کی قبول نہیں کی۔ سیٹنگز میں چیک کریں۔", "رفض TMDB مفتاح API. تحقق منه في الإعدادات.");
        a("e_net", "Could not reach TMDB (no internet or a server problem).", "TMDB تک رسائی نہیں ہو سکی (انٹرنیٹ نہیں یا سرور کا مسئلہ)۔", "تعذر الوصول إلى TMDB (لا إنترنت أو مشكلة في الخادم).");
        a("e_rate", "TMDB asked to slow down. Try again later.", "TMDB نے رفتار کم کرنے کو کہا۔ بعد میں کوشش کریں۔", "طلب TMDB التمهل. حاول لاحقًا.");
        a("e_nokey", "Add your TMDB API key in Settings first.", "پہلے سیٹنگز میں اپنی TMDB API کی ڈالیں۔", "أضف مفتاح TMDB API في الإعدادات أولاً.");
        a("e_norule", "Add at least one rule that is switched on.", "کم از کم ایک اصول بنائیں جو آن ہو۔", "أضف قاعدة واحدة مفعّلة على الأقل.");
        // rules
        a("r_add", "Add rule", "اصول شامل کریں", "أضف قاعدة");
        a("r_none", "No rules yet.", "ابھی کوئی اصول نہیں۔", "لا قواعد بعد.");
        a("r_edit", "Edit", "ترمیم", "تعديل");
        a("r_delete", "Delete", "حذف", "حذف");
        a("r_del_msg", "Delete this rule?", "کیا یہ اصول حذف کریں؟", "حذف هذه القاعدة؟");
        a("r_on", "On", "آن", "مفعّلة");
        a("r_name", "Rule name", "اصول کا نام", "اسم القاعدة");
        a("r_type", "Type", "قسم", "النوع");
        a("r_movies", "Movies", "فلمیں", "أفلام");
        a("r_tv", "TV shows", "سیریز", "مسلسلات");
        a("r_both", "Both", "دونوں", "كلاهما");
        a("r_genres", "Genres (leave empty for all)", "صنفیں (سب کے لیے خالی چھوڑیں)", "الأنواع (اتركها فارغة للكل)");
        a("r_all", "Must match ALL selected genres", "منتخب سب صنفوں سے میل کھانا ضروری", "يجب مطابقة كل الأنواع المحددة");
        a("r_kw", "Keywords (separate with commas)", "کی ورڈز (کاما سے الگ کریں)", "كلمات مفتاحية (افصل بفواصل)");
        a("r_kw_hint", "e.g. zombie, time travel, heist", "مثلاً zombie, time travel, heist", "مثل: zombie, time travel, heist");
        a("r_mode", "Keyword search", "کی ورڈ تلاش", "بحث الكلمات");
        a("r_mode_tmdb", "TMDB keywords (accurate, English)", "TMDB کی ورڈز (درست، انگریزی)", "كلمات TMDB (دقيق، إنجليزي)");
        a("r_mode_text", "Words in title or overview", "عنوان یا خلاصے میں الفاظ", "كلمات في العنوان أو الملخص");
        a("r_days", "Look back (days)", "کتنے دن پیچھے دیکھیں", "كم يومًا للخلف");
        a("r_save", "Save rule", "اصول محفوظ کریں", "حفظ القاعدة");
        a("r_cancel", "Cancel", "منسوخ", "إلغاء");
        a("r_everything", "Everything new", "سب کچھ نیا", "كل جديد");
        a("r_last_days", "last %d days", "پچھلے %d دن", "آخر %d يومًا");
        a("r_any_genre", "all genres", "تمام صنفیں", "كل الأنواع");
        a("r_kw_label", "keywords", "کی ورڈز", "كلمات");
        a("r_need_name", "Give the rule a name.", "اصول کا نام لکھیں۔", "أعطِ القاعدة اسمًا.");
        // settings
        a("s_key", "TMDB API key (v3)", "TMDB API کی (v3)", "مفتاح TMDB API (v3)");
        a("s_key_hint", "Paste your key here", "اپنی کی یہاں پیسٹ کریں", "الصق مفتاحك هنا");
        a("s_test", "Test key", "کی ٹیسٹ کریں", "اختبر المفتاح");
        a("s_test_ok", "The key works.", "کی درست ہے۔", "المفتاح يعمل.");
        a("s_region", "Country for Netflix availability (2 letters: PK, US, GB, IN, AE, SA...)", "نیٹ فلکس کی دستیابی کا ملک (2 حروف: PK, US, GB, IN, AE, SA...)", "دولة توفر نتفليكس (حرفان: PK, US, GB, IN, AE, SA...)");
        a("s_interval", "Automatic check", "خودکار چیک", "الفحص التلقائي");
        a("s_off", "Off", "بند", "إيقاف");
        a("s_h6", "Every 6 hours", "ہر 6 گھنٹے", "كل 6 ساعات");
        a("s_h12", "Every 12 hours", "ہر 12 گھنٹے", "كل 12 ساعة");
        a("s_h24", "Every 24 hours", "ہر 24 گھنٹے", "كل 24 ساعة");
        a("s_notify", "Notify me about new titles", "نئے ٹائٹلز کی اطلاع دیں", "نبّهني بالعناوين الجديدة");
        a("s_lang", "Language", "زبان", "اللغة");
        a("s_theme", "Theme", "تھیم", "المظهر");
        a("s_dark", "Dark", "ڈارک", "داكن");
        a("s_light", "Light", "لائٹ", "فاتح");
        a("s_save", "Save", "محفوظ کریں", "حفظ");
        a("s_saved", "Saved", "محفوظ ہو گیا", "تم الحفظ");
        a("s_about", "About, privacy and limits", "تعارف، پرائیویسی اور حدود", "حول والخصوصية والقيود");
        a("s_about_text", "How it works: the app asks TMDB for movies and TV shows that are on Netflix (flat-rate) in your country and were released or have new episodes in the last days you choose, filtered by your genres and keywords.\n\nWhat leaves your phone: your TMDB key, your country, and the genre, keyword and date filters go to api.themoviedb.org; poster pictures load from image.tmdb.org. Nothing else is sent, and the app does not see your Netflix account.\n\nLimits: Netflix has no public list of new titles, so availability comes from TMDB (JustWatch data) and can be late or incomplete. \"New\" means a recent release or new episodes, not the exact date Netflix added it. Catalogs differ by country. Automatic checks use Android's job scheduler: Xiaomi, Oppo, Vivo and Huawei phones need Autostart and no battery restriction for this app, and Android may delay checks.\n\nThis product uses the TMDB API but is not endorsed or certified by TMDB. Streaming availability data is provided by JustWatch. Netflix is a trademark of Netflix, Inc.; this app is not affiliated with Netflix.", "یہ کیسے کام کرتا ہے: ایپ TMDB سے وہ فلمیں اور سیریز مانگتی ہے جو آپ کے ملک میں نیٹ فلکس پر ہیں (فلیٹ ریٹ) اور جو آپ کے چنے ہوئے دنوں میں ریلیز ہوئیں یا جن کی نئی اقساط آئیں، آپ کی صنفوں اور کی ورڈز کے مطابق چھان کر۔\n\nفون سے کیا باہر جاتا ہے: آپ کی TMDB کی، ملک، اور صنف، کی ورڈ اور تاریخ کے فلٹر api.themoviedb.org کو جاتے ہیں؛ پوسٹر تصاویر image.tmdb.org سے آتی ہیں۔ اور کچھ نہیں بھیجا جاتا، اور ایپ آپ کا نیٹ فلکس اکاؤنٹ نہیں دیکھتی۔\n\nحدود: نیٹ فلکس نئے ٹائٹلز کی کوئی عوامی فہرست نہیں دیتا، اس لیے دستیابی TMDB (JustWatch ڈیٹا) سے آتی ہے اور دیر سے یا نامکمل ہو سکتی ہے۔ \"نیا\" کا مطلب حالیہ ریلیز یا نئی اقساط ہے، نیٹ فلکس پر شامل ہونے کی اصل تاریخ نہیں۔ ہر ملک کی کیٹلاگ مختلف ہے۔ خودکار چیک اینڈرائیڈ کے جاب شیڈیولر سے ہوتے ہیں: ژیاؤمی، اوپو، ویوو اور ہواوے فونز پر اس ایپ کے لیے Autostart اور بیٹری پابندی ہٹانا ضروری ہے، اور اینڈرائیڈ چیک میں تاخیر کر سکتا ہے۔\n\nیہ پروڈکٹ TMDB API استعمال کرتا ہے مگر TMDB کا توثیق شدہ نہیں۔ اسٹریمنگ دستیابی کا ڈیٹا JustWatch فراہم کرتا ہے۔ Netflix, Inc. کا ٹریڈ مارک ہے؛ یہ ایپ نیٹ فلکس سے وابستہ نہیں۔", "كيف يعمل: يطلب التطبيق من TMDB الأفلام والمسلسلات المتوفرة على نتفليكس (اشتراك) في بلدك والتي صدرت أو لها حلقات جديدة خلال الأيام التي تختارها، مصفّاة بأنواعك وكلماتك المفتاحية.\n\nما يغادر هاتفك: مفتاح TMDB وبلدك ومرشحات النوع والكلمات والتاريخ تذهب إلى api.themoviedb.org؛ وصور الملصقات تُحمّل من image.tmdb.org. لا يُرسل شيء آخر ولا يرى التطبيق حسابك في نتفليكس.\n\nالقيود: ليس لنتفليكس قائمة عامة بالعناوين الجديدة لذا تأتي التوفرية من TMDB (بيانات JustWatch) وقد تتأخر أو تكون ناقصة. \"جديد\" يعني إصدارًا حديثًا أو حلقات جديدة وليس تاريخ إضافته إلى نتفليكس بدقة. تختلف الفهارس حسب البلد. الفحص التلقائي يستخدم جدولة المهام في أندرويد: هواتف شاومي وأوبو وفيفو وهواوي تحتاج إلى Autostart وعدم تقييد البطارية لهذا التطبيق وقد يؤخر أندرويد الفحص.\n\nيستخدم هذا المنتج واجهة TMDB لكنه غير معتمد من TMDB. بيانات توفر البث من JustWatch. Netflix علامة تجارية لشركة Netflix, Inc.؛ هذا التطبيق غير تابع لنتفليكس.");
        a("nt_title", "%d new on Netflix", "نیٹ فلکس پر %d نئے", "%d جديد على نتفليكس");
    }

    static String t(Context c, String key) {
        String[] v = M.get(key);
        if (v == null) return key;
        return v[Store.langIdx(c)];
    }
}
