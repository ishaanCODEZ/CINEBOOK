import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** CineBook server: serves the website and answers chat questions with Claude. Run: java ChatServer.java */
public class ChatServer {
  static final String KEY = System.getenv("ANTHROPIC_API_KEY");
  static final String MODEL = Optional.ofNullable(System.getenv("CLAUDE_MODEL")).orElse("claude-sonnet-5-5");
  static final String SYSTEM = "You are CineBot, the friendly assistant of CineBook, a demo movie ticket booking website. "
    + "Answer in plain text, under 80 words, no markdown. Help users pick movies (use exact titles from the site data given at the start of the user message), "
    + "and explain how the site works. Facts: 8 theatres, 5 showtimes per movie per day, 5 days ahead. Seat rows A-B Premium (+Rs 120), C-D Executive (+Rs 60), E-F Classic. "
    + "Up to 8 seats per booking, seats held 5 minutes at checkout, Rs 20 convenience fee per ticket. Coupons: CINE10 (10 percent), FIRST100 (Rs 100 off), FAMILY20 (20 percent, needs Rs 1000+). "
    + "Payments are simulated and no money is charged. Cancel from My bookings. Tickets can be printed or added to a calendar. Support tickets are raised on the Support page. "
    + "You cannot make bookings yourself; tell users which page to open. If asked something unrelated to movies or the site, politely steer back.";
  static final HttpClient HTTP = HttpClient.newHttpClient();
  static final Path ROOT = Paths.get("").toAbsolutePath().normalize();

  public static void main(String[] args) throws Exception {
    int port = Integer.parseInt(Optional.ofNullable(System.getenv("PORT")).orElse("8080"));
    HttpServer s = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
    s.createContext("/api/chat", ChatServer::chat);
    s.createContext("/", ChatServer::file);
    s.start();
    System.out.println("CineBook running at http://localhost:" + port);
    System.out.println(KEY == null || KEY.isBlank() ? "AI chat: OFF (set ANTHROPIC_API_KEY to turn it on)" : "AI chat: ON (model " + MODEL + ")");
  }

  static void send(HttpExchange x, int code, String type, byte[] b) throws IOException {
    x.getResponseHeaders().set("Content-Type", type);
    x.sendResponseHeaders(code, b.length);
    try (OutputStream o = x.getResponseBody()) { o.write(b); }
  }
  static void json(HttpExchange x, int c, String s) throws IOException { send(x, c, "application/json; charset=utf-8", s.getBytes(StandardCharsets.UTF_8)); }

  static void file(HttpExchange x) throws IOException {
    String p = x.getRequestURI().getPath();
    if (p.equals("/")) p = "/index.html";
    Path f = ROOT.resolve(p.substring(1)).normalize();
    if (!f.startsWith(ROOT) || !Files.isRegularFile(f) || p.endsWith(".java") || p.endsWith(".bat")) { send(x, 404, "text/plain", "Not found".getBytes()); return; }
    String n = f.getFileName().toString();
    String t = n.endsWith(".html") ? "text/html; charset=utf-8" : n.endsWith(".css") ? "text/css; charset=utf-8" : n.endsWith(".js") ? "text/javascript; charset=utf-8" : n.endsWith(".svg") ? "image/svg+xml" : n.endsWith(".webmanifest") ? "application/manifest+json" : "application/octet-stream";
    send(x, 200, t, Files.readAllBytes(f));
  }

  static void chat(HttpExchange x) throws IOException {
    try {
      if (!x.getRequestMethod().equals("POST")) { json(x, 405, "{\"error\":\"method\"}"); return; }
      if (KEY == null || KEY.isBlank()) { json(x, 503, "{\"error\":\"no_key\"}"); return; }
      String b = new String(x.getRequestBody().readNBytes(30000), StandardCharsets.UTF_8);
      int i = b.indexOf('['), j = b.lastIndexOf(']');
      if (i < 0 || j < i) { json(x, 400, "{\"error\":\"bad_request\"}"); return; }
      String body = "{\"model\":\"" + MODEL + "\",\"max_tokens\":400,\"system\":\"" + esc(SYSTEM) + "\",\"messages\":" + b.substring(i, j + 1) + "}";
      HttpRequest r = HttpRequest.newBuilder(URI.create("https://api.anthropic.com/v1/messages"))
        .header("x-api-key", KEY).header("anthropic-version", "2023-06-01").header("content-type", "application/json")
        .timeout(java.time.Duration.ofSeconds(40)).POST(HttpRequest.BodyPublishers.ofString(body)).build();
      HttpResponse<String> res = HTTP.send(r, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
      if (res.statusCode() != 200) { System.out.println("API error " + res.statusCode() + ": " + res.body()); json(x, 502, "{\"error\":\"upstream\"}"); return; }
      json(x, 200, "{\"reply\":\"" + esc(text(res.body())) + "\"}");
    } catch (Exception e) { System.out.println(e); json(x, 500, "{\"error\":\"server\"}"); }
  }

  static String esc(String s) {
    StringBuilder o = new StringBuilder();
    for (char c : s.toCharArray()) {
      switch (c) {
        case '"': o.append("\\\""); break;
        case '\\': o.append("\\\\"); break;
        case '\n': o.append("\\n"); break;
        case '\r': break;
        case '\t': o.append("\\t"); break;
        default: if (c < 32) o.append(String.format("\\u%04x", (int) c)); else o.append(c);
      }
    }
    return o.toString();
  }

  static String text(String j) {
    int i = j.indexOf("\"text\":\"");
    if (i < 0) return "Sorry, I could not read the answer.";
    i += 8;
    StringBuilder o = new StringBuilder();
    for (; i < j.length(); i++) {
      char c = j.charAt(i);
      if (c == '"') break;
      if (c == '\\') {
        char n = j.charAt(++i);
        if (n == 'n') o.append('\n'); else if (n == 't') o.append('\t');
        else if (n == 'u') { o.append((char) Integer.parseInt(j.substring(i + 1, i + 5), 16)); i += 4; }
        else o.append(n);
      } else o.append(c);
    }
    return o.toString();
  }
}
