# FitTrack

**FitTrack** es una aplicación completa de seguimiento fitness. Permite a los usuarios crear rutinas personalizadas, planificar entrenamientos, llevar un control de rachas y localizar actividades mediante integración con Maps.

---

## 🚀 Características

- API REST construida con Laravel.
- Autenticación con token.
- Cliente Android para interactuar con la API.
- Soporte para Firebase (notificaciones, almacenamiento, etc.).
- Sistema de rachas y progreso.
- Integración con mapas para registrar rutas y ubicaciones.

---

## 📂 Estructura del proyecto

- **Laravel API**  
  Backend principal que gestiona usuarios, rutinas, estadísticas y tokens de acceso.
- **Android App**  
  Cliente móvil para usuarios finales.
- **Firebase**  
  Servicios complementarios: push notifications, almacenamiento de archivos, etc.

---

## 🔑 Autenticación

La API utiliza tokens cada petición al backend debe incluir el header:

Authorization: Bearer {token}
