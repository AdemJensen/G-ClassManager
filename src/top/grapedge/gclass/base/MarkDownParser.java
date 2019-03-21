package top.grapedge.gclass.base;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-19 12:50
 **/
public class MarkDownParser {
    private ArrayList<String> match(String text, String regex) {
        var list = new ArrayList<String>();
        var matcher = Pattern.compile(regex).matcher(text);
        while (matcher.find()) {
            list.add(matcher.group(0));
        }
        return list;
    }
    private String outdent(String text) {
        var find = match(text, "^(\\t| )+");
        return text.replace("^" + (find.size() > 0 ? find.get(0) : ""), "");
    }

    private String encodeAttr(String text) {
        return text.replaceAll("\"", "&quot;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    private Map<String, ArrayList<String>> TAGS = new HashMap<>();

    private <T> ArrayList<T> createArray(T... objects) {
        var list = new ArrayList<T>();
        Collections.addAll(list, objects);
        return list;
    }

    public MarkDownParser() {
        TAGS.put("", createArray("<em>", "</em>"));
        TAGS.put("_", createArray("<strong>", "</strong>"));
        TAGS.put("~", createArray("<s>", "</s>"));
        TAGS.put("\n", createArray("<br />"));
        TAGS.put(" ", createArray("<br />"));
        TAGS.put("-", createArray("<hr />"));
    }

    private String getChar(String str, int index) {
        return str.length() > index ? String.valueOf(str.charAt(index)) : "";
    }

    private List<String> context = new ArrayList<>();

    private String tag(String token) {
        var desc = TAGS.get(getChar(token.replaceAll("\\*", "_"), 1));
        var end = context.get(context.size() - 1).equals(token);

        if (desc != null) return token;
        if (desc.size() < 2) return desc.get(0);
        if (end) {
            context.remove(token);
        } else {
            context.add(token);
        }
        return desc.get(end ? 1 : 0);
    }

    private String flush() {
        var str = new StringBuilder();
        while (context.size() > 0) str.append(tag(context.get(context.size() - 1)));
        return str.toString();
    }

    private interface ReplaceListener {
        String onReplace(List<String> groups);
    }
    private String replaceAll(String text, String regex, ReplaceListener listener) {
        var m = Pattern.compile(regex).matcher(text);
        var result = new StringBuffer();
        while (m.find()) {
            var list = new ArrayList<String>();
            for (var i = 0; i <= m.groupCount(); i++) list.add(m.group(i));
            m.appendReplacement(result, listener.onReplace(list));
        }
        m.appendTail(result);
        return result.toString();
    }

    public String parse(String text) {
        try {
            var lines = text.split("\n");
            var builder = new StringBuilder();
            for (var i : lines) if (!i.isEmpty()) {
                builder.append(parse(i, null));
            } else {
                builder.append("<br />");
            }
            return builder.toString();
        } catch (Exception e) {
            return text;
        }
    }

    private String parse(String text, Map<String, String> prevLinks) {
        Map<String, String> links = prevLinks == null ? new HashMap<>() : prevLinks;
        var tokenizer = Pattern.compile("((?:^|\\n+)(?:\\n---+|\\* \\*(?: \\*)+)\\n)|(?:^``` *(\\w*)\\n([\\s\\S]*?)\\n```$)|((?:(?:^|\\n+)(?:\\t|  {2,}).+)+\\n*)|((?:(?:^|\\n)([>*+-]|\\d+\\.)\\s+.*)+)|(?:\\!\\[([^\\]]*?)\\]\\(([^\\)]+?)\\))|(\\[)|(\\](?:\\(([^\\)]+?)\\))?)|(?:(?:^|\\n+)([^\\s].*)\\n(\\-{3,}|={3,})(?:\\n+|$))|(?:(?:^|\\n+)(#{1,6})\\s*(.+)(?:\\n+|$))|(?:`([^`].*?)`)|(  \\n\\n*|\\n{2,}|__|\\*\\*|[_*]|~~)");
        text = replaceAll(text, "^\\[(.+?)\\]:\\s*(.+)$", e-> {
            links.put(e.get(1).toLowerCase(), e.get(2));
            return "";
        }).replaceAll("^\\n+|\\n+$", "");
        var token = tokenizer.matcher(text);
        int last = 0;
        var tmp = "";
        var out = new StringBuilder();
        while (token.find()) {
            var prev = text.substring(last, token.start());
            last = token.end();
            var chunk = token.group(0);
            if (prev.matches("[^\\\\](\\\\\\\\)*\\\\$")) {
                // TODO
            } else if (token.group(3) != null || token.group(4) != null) {
                chunk = "<pre class=\"code " +
                        (token.group(4) != null? "poetry" : token.group(2).toLowerCase())+"\">"+outdent(encodeAttr(token.group(3) != null ? token.group(3) : token.group(4)).replace("^\\n+|\\n+$", "")) + "</pre>";
            } else if (token.group(6) != null) {
                tmp = token.group(6);
                var token5 = token.group(5);
                if (tmp.matches("\\.")) {
                    token5 = token5.replaceAll("^\\d+", "");
                }
                var inner = parse(outdent(token5.replaceAll("^\\s*[>*+.-]", "")), null);
                if (tmp.equals(">")) tmp = "blockquote";
                else {
                    tmp = tmp.matches("\\.") ? "ol" : "ul";
                    inner = replaceAll(inner, "^(.*)(\\n|$)", e-> "<li>" + e.get(1) + "</li>");
                }
                chunk = "<" + tmp + ">" + inner + "</" + tmp + ">";
            } else if (token.group(8) != null) {
                chunk = "<img src=\"" + encodeAttr(token.group(8)) + "\" alt=\"" + encodeAttr(token.group(7)) + "\">";
            } else if (token.group(10) != null) {
                out = new StringBuilder(out.toString().replaceAll("<a>", "<a href=\"" + encodeAttr(token.group(11) != null ? token.group(11) : links.get(prev.toLowerCase())) + "\">"));
                chunk = flush() + "</a>";
            } else if (token.group(9) != null) {
                chunk = "<a>";
            } else if (token.group(12) != null || token.group(14) != null) {
                tmp = 'h' + String.valueOf(token.group(14) != null ? token.group(14).length() : (token.group(13).charAt(0) == '=' ? 1 : 2));
                chunk = '<'+tmp+'>' + parse(token.group(12) != null ? token.group(12) : token.group(15), links) + "</" +tmp+">";
            } else if (token.group(16) != null) {
                chunk = "<code>" + encodeAttr(token.group(16)) + "</code>";
            } else if (token.group(17) != null || token.group(1) != null) {
                chunk = tag(token.group(17) != null ? token.group(17) : "--");
            }
            out.append(prev);
            out.append(chunk);
        }
        return (out + text.substring(last) + flush()).trim();
    }

}
