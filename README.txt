CINEBOOK v8

OPTION 1 - no install: double-click index.html. Everything works; the chatbot answers in "Basic mode" (keyword rules).

OPTION 2 - AI chatbot (needs Java 11+ and an Anthropic API key):
  Windows: double-click run-windows.bat, paste your key, the site opens at http://localhost:8080
  Mac/Linux: export ANTHROPIC_API_KEY=your_key   then   java ChatServer.java   then open http://localhost:8080
The key stays on your computer; the browser never sees it. Optional: set CLAUDE_MODEL to change the model.

Demo login: demo@cinebook.com / demo123. Coupons: FIRST100, CINE10, FAMILY20 (min Rs 1000).

POSTERS: with internet, real movie posters are fetched from Wikipedia for every movie and cached in your browser. Without internet (or if a poster is not found) a generated poster is shown instead.

NEW IN v8: Ctrl+K quick search, voice search (Chrome/Edge), auto-pick best seats, live "filling fast" showtime meter, save ticket as PNG image, invite friends (share), and installable app mode (open via the Java server at http://localhost:8080, then use the browser's Install button).
