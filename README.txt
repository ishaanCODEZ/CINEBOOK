🎬 CinéBook – Movie Ticket Booking System

Agile (Scrum) final project: a movie ticket booking website. Browse 55 movies, pick a theatre and showtime, choose seats, apply a coupon, pay (simulated) and get a printable ticket.

Live demo: https://ishaancodez.github.io/CINEBOOK/
> Demo project: payments, theatres, showtimes and seat availability are sample data.

Features
- 55 movies, 8 theatres, search, filters, reviews and watchlist
- Seat map with Premium, Executive and Classic tiers, max 8 seats, auto-pick best seats
- 5-minute seat hold, coupons, simulated payment
- Ticket with print, PNG download and calendar file
- My bookings with cancellation, insights and CSV export
- Support ticket board and CinéBot chatbot (basic or AI mode)

Tech Stack
- Front end: HTML5, CSS3, vanilla JavaScript (single-page app, no framework)
- Storage: browser localStorage
- Posters: Wikipedia API
- Optional server: Java 11+ (`ChatServer.java`) forwarding chat to the Claude API
- Tools: Git, GitHub, GitHub Pages, Jira

## Run It
- No install: double-click `index.html`
- AI chatbot (Windows): double-click `run-windows.bat` and paste your Anthropic API key. Opens at http://localhost:8080
- Mac/Linux: `export ANTHROPIC_API_KEY=your_key` then `java ChatServer.java`

Demo Login and Coupons
`demo@cinebook.com` / `demo123`
Coupons: `FIRST100` (₹100 off), `CINE10` (10%), `FAMILY20` (20% on ₹1,000+)

## Team
| Member | Scrum Role | Responsibility |
|---|---|---|
| Ishaan Mantri | Scrum Master and Lead Developer | Led sprints, removed blockers, built and integrated the code, managed GitHub |
| Danson Craig | Product Owner | Jira documentation, README, design feedbacks |
| Dev Batra | QA Tester | Wrote and ran test cases, reported bugs |
| Aaditi | Documentation and UI Reviewer | Defined requirements, prioritised the backlog, wrote user storie |

Limitations
Data is stored per browser; passwords use a simple hash; payment and refunds are simulated; the QR code is decorative.

