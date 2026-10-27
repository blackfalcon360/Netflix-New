package com.blackfalcon.netflixnew;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Tiny JSON reader/writer (pure Java, so it can be tested on a computer). Numbers are Double, objects LinkedHashMap. */
final class Json {
    private Json() {}

    // ---------- reading ----------

    static Object parse(String s) {
        P p = new P(s);
        p.ws();
        Object v = p.value();
        p.ws();
        if (p.i != s.length()) throw new IllegalArgumentException("trailing data at " + p.i);
        return v;
    }

    private static final class P {
        final String s;
        int i = 0;

        P(String s) { this.s = s; }

        void ws() {
            while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
        }

        Object value() {
            if (i >= s.length()) throw new IllegalArgumentException("unexpected end");
            char c = s.charAt(i);
            if (c == '{') return obj();
            if (c == '[') return arr();
            if (c == '"') return str();
            if (s.startsWith("true", i)) { i += 4; return Boolean.TRUE; }
            if (s.startsWith("false", i)) { i += 5; return Boolean.FALSE; }
            if (s.startsWith("null", i)) { i += 4; return null; }
            return num();
        }

        Map<String, Object> obj() {
            LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
            i++;
            ws();
            if (s.charAt(i) == '}') { i++; return m; }
            while (true) {
                ws();
                String k = str();
                ws();
                if (s.charAt(i) != ':') throw new IllegalArgumentException("expected : at " + i);
                i++;
                ws();
                m.put(k, value());
                ws();
                char c = s.charAt(i++);
                if (c == '}') return m;
                if (c != ',') throw new IllegalArgumentException("expected , at " + (i - 1));
            }
        }

        List<Object> arr() {
            ArrayList<Object> l = new ArrayList<Object>();
            i++;
            ws();
            if (s.charAt(i) == ']') { i++; return l; }
            while (true) {
                ws();
                l.add(value());
                ws();
                char c = s.charAt(i++);
                if (c == ']') return l;
                if (c != ',') throw new IllegalArgumentException("expected , at " + (i - 1));
            }
        }

        String str() {
            if (s.charAt(i) != '"') throw new IllegalArgumentException("expected string at " + i);
            i++;
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = s.charAt(i++);
                if (c == '"') return sb.toString();
                if (c == '\\') {
                    char e = s.charAt(i++);
                    switch (e) {
                        case 'n': sb.append('\n'); break;
                        case 't': sb.append('\t'); break;
                        case 'r': sb.append('\r'); break;
                        case 'b': sb.append('\b'); break;
                        case 'f': sb.append('\f'); break;
                        case 'u':
                            sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
                            i += 4;
                            break;
                        default: sb.append(e); break; // " \ /
                    }
                } else {
                    sb.append(c);
                }
            }
        }

        Double num() {
            int st = i;
            while (i < s.length() && "+-0123456789.eE".indexOf(s.charAt(i)) >= 0) i++;
            if (st == i) throw new IllegalArgumentException("bad value at " + i);
            return Double.valueOf(s.substring(st, i));
        }
    }

    // ---------- helpers ----------

    @SuppressWarnings("unchecked")
    static Map<String, Object> map(Object o) {
        return o instanceof Map ? (Map<String, Object>) o : null;
    }

    @SuppressWarnings("unchecked")
    static List<Object> list(Object o) {
        return o instanceof List ? (List<Object>) o : new ArrayList<Object>();
    }

    static String str(Map<String, Object> m, String k, String def) {
        Object o = m == null ? null : m.get(k);
        return o instanceof String ? (String) o : def;
    }

    static long num(Map<String, Object> m, String k, long def) {
        Object o = m == null ? null : m.get(k);
        return o instanceof Number ? ((Number) o).longValue() : def;
    }

    static double dbl(Map<String, Object> m, String k, double def) {
        Object o = m == null ? null : m.get(k);
        return o instanceof Number ? ((Number) o).doubleValue() : def;
    }

    static boolean bool(Map<String, Object> m, String k, boolean def) {
        Object o = m == null ? null : m.get(k);
        return o instanceof Boolean ? (Boolean) o : def;
    }

    // ---------- writing ----------

    static String write(Object o) {
        StringBuilder sb = new StringBuilder();
        w(sb, o);
        return sb.toString();
    }

    private static void w(StringBuilder sb, Object o) {
        if (o == null) {
            sb.append("null");
        } else if (o instanceof String) {
            q(sb, (String) o);
        } else if (o instanceof Boolean) {
            sb.append(o.toString());
        } else if (o instanceof Number) {
            double d = ((Number) o).doubleValue();
            if (d == Math.rint(d) && Math.abs(d) < 1e15) sb.append((long) d);
            else sb.append(d);
        } else if (o instanceof Map) {
            sb.append('{');
            boolean first = true;
            for (Map.Entry<?, ?> e : ((Map<?, ?>) o).entrySet()) {
                if (!first) sb.append(',');
                first = false;
                q(sb, String.valueOf(e.getKey()));
                sb.append(':');
                w(sb, e.getValue());
            }
            sb.append('}');
        } else if (o instanceof List) {
            sb.append('[');
            boolean first = true;
            for (Object x : (List<?>) o) {
                if (!first) sb.append(',');
                first = false;
                w(sb, x);
            }
            sb.append(']');
        } else {
            q(sb, o.toString());
        }
    }

    private static void q(StringBuilder sb, String s) {
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        sb.append('"');
    }
}
