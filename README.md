# fya-creditos-backend

Backend for the Fya Social Capital credit registration/lookup technical test.

## Deployment notes (Render)

- **Free tier cold start**: this service is deployed on Render's free plan, which spins the instance down after ~15 minutes of inactivity. The first request after idle time can take 30-60 seconds while it wakes up; subsequent requests are fast.
- **Outbound SMTP ports are blocked**: Render blocks outbound connections on the standard SMTP ports (25, 465, 587) on free/starter plans to prevent abuse. The async email notification (see `MailService`) uses Brevo's SMTP relay on the alternate port **2525**, which is not blocked. Locally/Docker this restriction doesn't apply, so any SMTP provider/port works.
