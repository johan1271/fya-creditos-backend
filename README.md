🇬🇧 English | 🇪🇸 [Español](#español)

# fya-creditos-backend

Backend for the Fya Social Capital credit registration/lookup technical test.

## Deployment notes (Render)

- **Free tier cold start**: this service is deployed on Render's free plan, which spins the instance down after ~15 minutes of inactivity. The first request after idle time can take 30-60 seconds while it wakes up; subsequent requests are fast.
- **Outbound SMTP ports are blocked**: Render blocks outbound connections on the standard SMTP ports (25, 465, 587) on free/starter plans to prevent abuse. The async email notification (see `MailService`) uses Brevo's SMTP relay on the alternate port **2525**, which is not blocked. Locally/Docker this restriction doesn't apply, so any SMTP provider/port works.

---

## Español

# fya-creditos-backend

Backend para la prueba técnica de registro/consulta de créditos de Fya Social Capital.

## Notas de despliegue (Render)

- **Cold start del plan free**: este servicio está desplegado en el plan gratuito de Render, que apaga la instancia luego de ~15 minutos de inactividad. La primera petición después de estar inactivo puede tardar 30-60 segundos en despertar; las siguientes son rápidas.
- **Los puertos SMTP salientes están bloqueados**: Render bloquea las conexiones salientes por los puertos estándar de SMTP (25, 465, 587) en los planes free/starter para prevenir abuso. La notificación de correo asíncrona (ver `MailService`) usa el relay SMTP de Brevo por el puerto alternativo **2525**, que no está bloqueado. En local/Docker esta restricción no aplica, así que cualquier proveedor/puerto SMTP funciona.
