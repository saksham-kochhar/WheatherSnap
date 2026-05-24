# WeatherSnap 🌤️📸

A weather reporting Android app that combines live weather data with camera evidence.
Search any city, get real-time weather, capture a compressed photo, add field notes,
and save the full report locally.

---

## Features

- 🔍 **City Search** — autocomplete suggestions via Open-Meteo Geocoding API
- 🌡️ **Live Weather** — temperature, humidity, wind speed, pressure
- 📷 **CameraX Integration** — live preview with one-tap capture
- 🗜️ **Image Compression** — auto-scales and compresses to 60% JPEG quality, shows original vs compressed size
- 📋 **Report Creation** — combine weather data + photo + field notes into one report
- 💾 **Local Storage** — reports saved to Room database, images to device gallery (Pictures/WeatherSnap)
- 🗂️ **Report History** — view all saved reports with photo, weather, size stats, and delete option
- 🟢 **Permission Handling** — runtime camera permission with readiness indicator on home screen

---

## Tech Stack

| Layer | Technology |
|---|---|
| UI | Jetpack Compose + Material3 |
| Architecture | MVVM + StateFlow |
| Camera | CameraX (core, camera2, lifecycle, view) |
| Networking | Retrofit + Gson |
| Local DB | Room |
| Image Loading | Coil |
| Navigation | Navigation Compose |
| DI | Manual (AndroidViewModel) |

---

## APIs Used

- [Open-Meteo Weather API](https://open-meteo.com/) — free, no key required
- [Open-Meteo Geocoding API](https://open-meteo.com/en/docs/geocoding-api) — city search

---

- [Open-Meteo Weather API](https://open-meteo.com/) — free, no key required
- [Open-Meteo Geocoding API](https://open-meteo.com/en/docs/geocoding-api) — city search

---

## Project Structure
