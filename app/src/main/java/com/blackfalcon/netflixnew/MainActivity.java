package com.blackfalcon.netflixnew;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity {

    private Ui ui;
    private FrameLayout content;
    private final Button[] tabB = new Button[3];
    private int tab = 0;
    private boolean checking = false;
    private String checkError = "";

    // rule editor state
    private Rule editing = null;
    private boolean editingNew = false;
    private EditText etName, etKw, etDays;
    private RadioGroup rgType, rgMode;
    private CheckBox cbAll;
    private final List<CheckBox> genreBoxes = new ArrayList<CheckBox>();
    private final List<String> genreKeys = Genres.keys();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = new Ui(this);
        if (!Store.hasRulesKey(this)) {
            Rule r = new Rule();
            r.id = newId();
            r.name = ui.t("r_everything");
            List<Rule> l = new ArrayList<Rule>();
            l.add(r);
            Store.saveRules(this, l);
        }
        build();
        Sched.apply(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderTab();
    }

    @Override
    public void onBackPressed() {
        if (editing != null) {
            editing = null;
            renderTab();
            return;
        }
        ui.dlg(this).setTitle(t("exit_title")).setMessage(t("exit_msg"))
                .setPositiveButton(t("yes"), (d, w) -> finish())
                .setNegativeButton(t("no"), null)
                .show();
    }

    // ---------- helpers ----------

    private String t(String k) { return ui.t(k); }

    private int lang() { return Store.langIdx(this); }

    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_LONG).show(); }

    private static String newId() { return Long.toString(System.currentTimeMillis(), 36) + Integer.toString((int) (Math.random() * 1000), 36); }

    private String errText(String code) {
        if ("nokey".equals(code)) return t("e_nokey");
        if ("norule".equals(code)) return t("e_norule");
        if ("HTTP 401".equals(code)) return t("e_key");
        if ("HTTP 429".equals(code)) return t("e_rate");
        return t("e_net");
    }

    private void openUrl(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            toast(t("error"));
        }
    }

    private void askNotif() {
        if (Build.VERSION.SDK_INT >= 33) requestNotifPermission();
    }

    @TargetApi(33)
    private void requestNotifPermission() {
        if (!Notifier.allowed(this)) requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 5);
    }

    // ---------- shell ----------

    private void build() {
        ui.refresh();
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(ui.bg);
        root.setPadding(ui.dp(8), ui.dp(8), ui.dp(8), ui.dp(6));
        root.setLayoutDirection("en".equals(Store.lang(this))
                ? View.LAYOUT_DIRECTION_LTR : View.LAYOUT_DIRECTION_RTL);

        content = new FrameLayout(this);
        root.addView(content, new LinearLayout.LayoutParams(Ui.MATCH, 0, 1f));

        LinearLayout bar = ui.row(root);
        String[] keys = {"tab_new", "tab_rules", "tab_settings"};
        String[] icons = {"\uD83C\uDFAC ", "\uD83D\uDCCB ", "\u2699\uFE0F "};
        for (int i = 0; i < 3; i++) {
            final int idx = i;
            tabB[i] = ui.btn(icons[i] + t(keys[i]), Ui.GRAY, v -> {
                tab = idx;
                editing = null;
                renderTab();
            });
            tabB[i].setSingleLine(true);
            bar.addView(tabB[i], ui.weighted());
        }
        TextView by = ui.tv(t("by"), 13, ui.sub);
        by.setGravity(Gravity.CENTER);
        by.setPadding(0, ui.dp(4), 0, 0);
        root.addView(by, new LinearLayout.LayoutParams(Ui.MATCH, Ui.WRAP));
        setContentView(root);
        renderTab();
    }

    private void renderTab() {
        if (content == null) return;
        content.removeAllViews();
        for (int i = 0; i < 3; i++) tabB[i].setBackground(ui.rounded(i == tab ? Ui.RED : Ui.GRAY, 10));
        View v;
        if (tab == 0) v = buildNew();
        else if (tab == 1) v = editing != null ? buildEditor() : buildRules();
        else v = buildSettings();
        ScrollView sv = new ScrollView(this);
        sv.addView(v, new ViewGroup.LayoutParams(Ui.MATCH, Ui.WRAP));
        content.addView(sv, new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
    }

    // ---------- New tab ----------

    private void checkNow() {
        if (checking) return;
        checking = true;
        renderTab();
        new Thread(() -> {
            final String err = Checker.run(this, false);
            runOnUiThread(() -> {
                checking = false;
                checkError = err == null ? "" : err;
                renderTab();
            });
        }).start();
    }

    private TextView badge(String text, int color) {
        TextView b = ui.tv(text, 10, Color.WHITE);
        b.setTypeface(null, android.graphics.Typeface.BOLD);
        b.setBackground(ui.rounded(color, 6));
        b.setPadding(ui.dp(6), ui.dp(1), ui.dp(6), ui.dp(1));
        return b;
    }

    private View buildNew() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);

        if (Store.apiKey(this).isEmpty()) {
            LinearLayout c = ui.card();
            c.addView(ui.tv(t("n_setup_title"), 16, ui.tx));
            TextView m = ui.tv(t("n_setup"), 13, ui.sub);
            m.setPadding(0, ui.dp(6), 0, ui.dp(6));
            c.addView(m);
            c.addView(ui.btn(t("n_go"), Ui.GREEN, v -> {
                tab = 2;
                renderTab();
            }), new LinearLayout.LayoutParams(Ui.MATCH, Ui.WRAP));
            box.addView(c, ui.gap());
            return box;
        }

        Results.Data d = Results.load(this);
        long last = Store.lastCheck(this);
        LinearLayout top = ui.row(box);
        Button chk = ui.btn(checking ? t("n_checking") : t("n_check"), checking ? Ui.GRAY : Ui.RED, v -> checkNow());
        top.addView(chk, ui.weighted());
        String lastErr = checking ? "" : (checkError.isEmpty() ? Store.lastError(this) : checkError);
        TextView st = ui.tv(last == 0 ? t("n_never")
                : String.format(Locale.US, t("n_last"), new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(new Date(last))), 12, ui.sub);
        top.addView(st, new LinearLayout.LayoutParams(0, Ui.WRAP, 1f));
        if (!lastErr.isEmpty()) {
            TextView e = ui.tv(errText(lastErr), 13, 0xFFFFA726);
            e.setPadding(ui.dp(4), ui.dp(4), ui.dp(4), ui.dp(4));
            box.addView(e);
        }

        int fresh = 0;
        for (Tmdb.Title x : d.titles) if (x.fresh) fresh++;
        if (fresh > 0) {
            box.addView(ui.btn(t("n_seen") + " (" + fresh + ")", Ui.GRAY, v -> {
                Results.clearFresh(this);
                renderTab();
            }), new LinearLayout.LayoutParams(Ui.MATCH, Ui.WRAP));
        }
        if (d.titles.isEmpty() && last != 0 && lastErr.isEmpty()) {
            TextView e = ui.tv(t("n_empty"), 14, ui.sub);
            e.setPadding(ui.dp(4), ui.dp(10), ui.dp(4), 0);
            box.addView(e);
        }

        LinkedHashMap<String, List<Tmdb.Title>> groups = new LinkedHashMap<String, List<Tmdb.Title>>();
        for (Tmdb.Title x : d.titles) {
            List<Tmdb.Title> l = groups.get(x.rule);
            if (l == null) {
                l = new ArrayList<Tmdb.Title>();
                groups.put(x.rule, l);
            }
            l.add(x);
        }
        for (Map.Entry<String, List<Tmdb.Title>> g : groups.entrySet()) {
            TextView h = ui.tv(g.getKey() + " (" + g.getValue().size() + ")", 16, ui.tx);
            h.setTypeface(null, android.graphics.Typeface.BOLD);
            h.setPadding(ui.dp(4), ui.dp(12), ui.dp(4), ui.dp(6));
            box.addView(h);
            for (Tmdb.Title x : g.getValue()) box.addView(titleCard(x), ui.gap());
        }
        TextView n = ui.tv(t("n_baseline"), 11, ui.sub);
        n.setPadding(ui.dp(4), ui.dp(6), ui.dp(4), 0);
        box.addView(n);
        return box;
    }

    private View titleCard(final Tmdb.Title x) {
        LinearLayout c = ui.card();
        LinearLayout row = ui.row(c);
        row.setGravity(Gravity.TOP);
        ImageView poster = new ImageView(this);
        poster.setBackground(ui.rounded(0xFF37474F, 6));
        poster.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(ui.dp(90), ui.dp(135));
        pp.setMargins(0, 0, ui.dp(10), 0);
        row.addView(poster, pp);
        Posters.load(x.poster, poster);

        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        row.addView(col, new LinearLayout.LayoutParams(0, Ui.WRAP, 1f));

        TextView name = ui.tv(x.title, 16, ui.tx);
        name.setTypeface(null, android.graphics.Typeface.BOLD);
        col.addView(name);

        LinearLayout bs = ui.row(col);
        if (x.fresh) {
            LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(Ui.WRAP, Ui.WRAP);
            bp.setMargins(0, ui.dp(3), ui.dp(6), ui.dp(3));
            bs.addView(badge(t("n_new"), Ui.RED), bp);
        }
        if (x.newEpisodes) {
            LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(Ui.WRAP, Ui.WRAP);
            bp.setMargins(0, ui.dp(3), ui.dp(6), ui.dp(3));
            bs.addView(badge(t("n_eps"), Ui.ORANGE), bp);
        }
        String meta = t(x.tv ? "n_tv" : "n_movie") + (x.date.isEmpty() ? "" : "  \u2022  " + x.date)
                + (x.vote > 0 ? String.format(Locale.US, "  \u2022  \u2605 %.1f", x.vote) : "");
        col.addView(ui.tv(meta, 12, ui.sub));

        StringBuilder gs = new StringBuilder();
        int shown = 0;
        for (int id : x.genres) {
            String gname = Genres.nameById(id, x.tv, lang());
            if (gname.isEmpty() || shown >= 3) continue;
            if (gs.length() > 0) gs.append(", ");
            gs.append(gname);
            shown++;
        }
        if (gs.length() > 0) col.addView(ui.tv(gs.toString(), 12, ui.sub));

        if (!x.overview.isEmpty()) {
            TextView ov = ui.tv(x.overview, 12, ui.tx);
            ov.setMaxLines(4);
            ov.setEllipsize(TextUtils.TruncateAt.END);
            ov.setPadding(0, ui.dp(4), 0, ui.dp(4));
            col.addView(ov);
        }
        LinearLayout br = ui.row(col);
        br.addView(ui.btn(t("n_netflix"), Ui.RED, v -> {
            try {
                openUrl("https://www.netflix.com/search?q=" + URLEncoder.encode(x.title, "UTF-8"));
            } catch (Exception e) {
                toast(t("error"));
            }
        }), ui.weighted());
        br.addView(ui.btn(t("n_tmdb"), Ui.BLUE, v -> openUrl("https://www.themoviedb.org/" + (x.tv ? "tv/" : "movie/") + x.id)), ui.weighted());
        return c;
    }

    // ---------- Rules tab ----------

    private String summary(Rule r) {
        List<String> parts = new ArrayList<String>();
        parts.add(t(r.type == Rule.MOVIES ? "r_movies" : (r.type == Rule.TV ? "r_tv" : "r_both")));
        if (r.genres.isEmpty()) {
            parts.add(t("r_any_genre"));
        } else {
            StringBuilder g = new StringBuilder();
            for (String k : r.genres) {
                if (g.length() > 0) g.append(r.matchAll ? " + " : ", ");
                g.append(Genres.name(k, lang()));
            }
            parts.add(g.toString());
        }
        if (!r.keywords.isEmpty()) parts.add(t("r_kw_label") + ": " + TextUtils.join(", ", r.keywords));
        parts.add(String.format(Locale.US, t("r_last_days"), r.days));
        return TextUtils.join("  \u2022  ", parts);
    }

    private View buildRules() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.addView(ui.btn("+ " + t("r_add"), Ui.GREEN, v -> {
            editing = new Rule();
            editing.id = newId();
            editingNew = true;
            renderTab();
        }), ui.gap());
        final List<Rule> rules = Store.rules(this);
        if (rules.isEmpty()) box.addView(ui.tv(t("r_none"), 14, ui.sub));
        for (final Rule r : rules) {
            LinearLayout c = ui.card();
            TextView nm = ui.tv(r.name, 16, ui.tx);
            nm.setTypeface(null, android.graphics.Typeface.BOLD);
            c.addView(nm);
            TextView sm = ui.tv(summary(r), 12, ui.sub);
            sm.setPadding(0, ui.dp(2), 0, ui.dp(4));
            c.addView(sm);
            LinearLayout row = ui.row(c);
            CheckBox on = new CheckBox(this);
            on.setText(t("r_on"));
            on.setChecked(r.enabled);
            ui.tint(on);
            on.setOnCheckedChangeListener((btn, checked) -> {
                r.enabled = checked;
                Store.saveRules(this, rules);
            });
            row.addView(on, new LinearLayout.LayoutParams(0, Ui.WRAP, 1f));
            row.addView(ui.btn(t("r_edit"), Ui.BLUE, v -> {
                editing = r;
                editingNew = false;
                renderTab();
            }), new LinearLayout.LayoutParams(Ui.WRAP, Ui.WRAP));
            row.addView(ui.btn(t("r_delete"), Ui.RED, v -> ui.dlg(this).setMessage(t("r_del_msg")).setTitle(r.name)
                    .setPositiveButton(t("yes"), (d, w) -> {
                        rules.remove(r);
                        Store.saveRules(this, rules);
                        Store.forgetRule(this, r.id);
                        renderTab();
                    })
                    .setNegativeButton(t("no"), null)
                    .show()), new LinearLayout.LayoutParams(Ui.WRAP, Ui.WRAP));
            box.addView(c, ui.gap());
        }
        return box;
    }

    private void addRadio(RadioGroup g, int id, String text, boolean checked) {
        RadioButton rb = new RadioButton(this);
        rb.setId(id);
        rb.setText(text);
        ui.tint(rb);
        g.addView(rb);
        if (checked) rb.setChecked(true);
    }

    private TextView label(String s) {
        TextView l = ui.tv(s, 14, ui.tx);
        l.setPadding(ui.dp(2), ui.dp(10), ui.dp(2), ui.dp(4));
        return l;
    }

    private View buildEditor() {
        final Rule r = editing;
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);

        box.addView(label(t("r_name")));
        etName = ui.input(t("r_name"));
        etName.setText(r.name);
        box.addView(etName, new LinearLayout.LayoutParams(Ui.MATCH, Ui.WRAP));

        box.addView(label(t("r_type")));
        rgType = new RadioGroup(this);
        rgType.setOrientation(RadioGroup.HORIZONTAL);
        addRadio(rgType, 201, t("r_movies"), r.type == Rule.MOVIES);
        addRadio(rgType, 202, t("r_tv"), r.type == Rule.TV);
        addRadio(rgType, 203, t("r_both"), r.type == Rule.BOTH);
        box.addView(rgType);

        box.addView(label(t("r_genres")));
        genreBoxes.clear();
        LinearLayout row = null;
        for (int i = 0; i < genreKeys.size(); i++) {
            if (i % 2 == 0) row = ui.row(box);
            CheckBox cb = new CheckBox(this);
            cb.setText(Genres.name(genreKeys.get(i), lang()));
            cb.setChecked(r.genres.contains(genreKeys.get(i)));
            ui.tint(cb);
            genreBoxes.add(cb);
            row.addView(cb, new LinearLayout.LayoutParams(0, Ui.WRAP, 1f));
        }
        cbAll = new CheckBox(this);
        cbAll.setText(t("r_all"));
        cbAll.setChecked(r.matchAll);
        ui.tint(cbAll);
        box.addView(cbAll);

        box.addView(label(t("r_kw")));
        etKw = ui.input(t("r_kw_hint"));
        etKw.setText(TextUtils.join(", ", r.keywords));
        box.addView(etKw, new LinearLayout.LayoutParams(Ui.MATCH, Ui.WRAP));

        box.addView(label(t("r_mode")));
        rgMode = new RadioGroup(this);
        addRadio(rgMode, 211, t("r_mode_tmdb"), !r.textMode);
        addRadio(rgMode, 212, t("r_mode_text"), r.textMode);
        box.addView(rgMode);

        box.addView(label(t("r_days")));
        etDays = ui.input("30");
        etDays.setInputType(InputType.TYPE_CLASS_NUMBER);
        etDays.setText(String.valueOf(r.days));
        box.addView(etDays, new LinearLayout.LayoutParams(Ui.MATCH, Ui.WRAP));

        LinearLayout bar = ui.row(box);
        bar.setPadding(0, ui.dp(12), 0, 0);
        bar.addView(ui.btn(t("r_save"), Ui.GREEN, v -> saveEditor()), ui.weighted());
        bar.addView(ui.btn(t("r_cancel"), Ui.GRAY, v -> {
            editing = null;
            renderTab();
        }), ui.weighted());
        return box;
    }

    private void saveEditor() {
        Rule r = editing;
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            toast(t("r_need_name"));
            return;
        }
        r.name = name;
        int id = rgType.getCheckedRadioButtonId();
        r.type = id == 201 ? Rule.MOVIES : (id == 202 ? Rule.TV : Rule.BOTH);
        r.genres = new ArrayList<String>();
        for (int i = 0; i < genreBoxes.size(); i++) if (genreBoxes.get(i).isChecked()) r.genres.add(genreKeys.get(i));
        r.matchAll = cbAll.isChecked();
        r.keywords = Rule.splitKeywords(etKw.getText().toString());
        r.textMode = rgMode.getCheckedRadioButtonId() == 212;
        int days = 30;
        try {
            days = Integer.parseInt(etDays.getText().toString().trim());
        } catch (Exception e) {
            days = 30;
        }
        r.days = Math.max(1, Math.min(365, days));
        List<Rule> rules = Store.rules(this);
        boolean found = false;
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).id.equals(r.id)) {
                rules.set(i, r);
                found = true;
            }
        }
        if (!found) rules.add(r);
        Store.saveRules(this, rules);
        Store.forgetRule(this, r.id); // a changed rule starts a fresh baseline
        editing = null;
        renderTab();
    }

    // ---------- Settings tab ----------

    private View buildSettings() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);

        LinearLayout c1 = ui.card();
        c1.addView(ui.tv(t("s_key"), 15, ui.tx));
        final EditText etKey = ui.input(t("s_key_hint"));
        etKey.setText(Store.apiKey(this));
        LinearLayout.LayoutParams kp = ui.gap();
        kp.setMargins(0, ui.dp(6), 0, ui.dp(6));
        c1.addView(etKey, kp);
        c1.addView(ui.btn(t("s_test"), Ui.BLUE, v -> testKey(etKey.getText().toString().trim())),
                new LinearLayout.LayoutParams(Ui.MATCH, Ui.WRAP));
        TextView rl = ui.tv(t("s_region"), 13, ui.tx);
        rl.setPadding(0, ui.dp(10), 0, ui.dp(4));
        c1.addView(rl);
        final EditText etReg = ui.input("PK");
        etReg.setText(Store.region(this));
        c1.addView(etReg, new LinearLayout.LayoutParams(Ui.MATCH, Ui.WRAP));
        box.addView(c1, ui.gap());

        LinearLayout c2 = ui.card();
        c2.addView(ui.tv(t("s_interval"), 15, ui.tx));
        final RadioGroup rgI = new RadioGroup(this);
        int h = Store.intervalHours(this);
        addRadio(rgI, 301, t("s_off"), h <= 0);
        addRadio(rgI, 306, t("s_h6"), h == 6);
        addRadio(rgI, 312, t("s_h12"), h == 12);
        addRadio(rgI, 324, t("s_h24"), h >= 24);
        c2.addView(rgI);
        final CheckBox cbN = new CheckBox(this);
        cbN.setText(t("s_notify"));
        cbN.setChecked(Store.notify(this));
        ui.tint(cbN);
        c2.addView(cbN);
        box.addView(c2, ui.gap());

        LinearLayout c3 = ui.card();
        c3.addView(ui.tv(t("s_lang"), 15, ui.tx));
        final RadioGroup rgL = new RadioGroup(this);
        String cur = Store.lang(this);
        addRadio(rgL, 101, "English", "en".equals(cur));
        addRadio(rgL, 102, "\u0627\u0631\u062F\u0648", "ur".equals(cur));
        addRadio(rgL, 103, "\u0627\u0644\u0639\u0631\u0628\u064A\u0629", "ar".equals(cur));
        c3.addView(rgL);
        c3.addView(ui.tv(t("s_theme"), 15, ui.tx));
        final RadioGroup rgT = new RadioGroup(this);
        addRadio(rgT, 111, t("s_dark"), Store.dark(this));
        addRadio(rgT, 112, t("s_light"), !Store.dark(this));
        c3.addView(rgT);
        box.addView(c3, ui.gap());

        box.addView(ui.btn(t("s_save"), Ui.RED, v -> {
            Store.setApiKey(this, etKey.getText().toString());
            String reg = etReg.getText().toString().trim().toUpperCase(Locale.ROOT);
            Store.setRegion(this, reg.matches("[A-Z]{2}") ? reg : "PK");
            int id = rgI.getCheckedRadioButtonId();
            Store.setIntervalHours(this, id == 301 ? 0 : (id == 306 ? 6 : (id == 312 ? 12 : 24)));
            Store.setNotify(this, cbN.isChecked());
            int lid = rgL.getCheckedRadioButtonId();
            Store.setLang(this, lid == 102 ? "ur" : (lid == 103 ? "ar" : "en"));
            Store.setDark(this, rgT.getCheckedRadioButtonId() != 112);
            Sched.apply(this);
            if (cbN.isChecked()) askNotif();
            build();
            tab = 2;
            renderTab();
            toast(t("s_saved"));
        }), ui.gap());

        LinearLayout c4 = ui.card();
        c4.addView(ui.tv(t("s_about"), 16, ui.tx));
        TextView body = ui.tv(t("s_about_text"), 13, ui.sub);
        body.setPadding(0, ui.dp(6), 0, 0);
        c4.addView(body);
        box.addView(c4, ui.gap());
        return box;
    }

    private void testKey(final String key) {
        if (key.isEmpty()) {
            toast(t("e_nokey"));
            return;
        }
        new Thread(() -> {
            String res;
            try {
                Tmdb.Cfg cfg = new Tmdb.Cfg();
                cfg.key = key;
                String body = new UrlHttp().get(Tmdb.configUrl(cfg));
                res = body.contains("images") ? null : "net";
            } catch (Exception e) {
                String m = e.getMessage() == null ? "" : e.getMessage();
                res = m.startsWith("HTTP 401") || m.startsWith("HTTP 403") ? "HTTP 401" : "net";
            }
            final String r = res;
            runOnUiThread(() -> toast(r == null ? t("s_test_ok") : errText(r)));
        }).start();
    }
}
