# 🕌 Prayer Times App

## 📱 About

Prayer Times is a modern Android application designed to help Muslims track their daily prayers. Built with Material Design 3 principles and a clean architecture approach, it offers a user-friendly interface and comprehensive features for managing prayer times and religious practices.

## ✨ Features

### Prayer Time Management
- 🌍 Location-based prayer times
- 🔔 Customizable prayer notifications with Adhan
- 📅 Daily, weekly, and monthly prayer calendar
- 🕰️ Home screen widget for quick access
- 🌐 Offline support for prayer times

### Religious Content
- 📖 Quran with translations
- 🤲 Collection of Duas (Prayers)
- 📚 Daily Hadiths
- ✨ Asma ul-Husna (99 Names of Allah)
- ⭐️ Favorites system for Duas and Hadiths

### Tools & Utilities
- 🎯 Qibla compass with gyroscope support
- 📊 Prayer statistics and tracking
- 📍 Nearby mosques finder
- 🌙 Ramadan timetable
- ⚙️ Customizable settings

### Technical Features
- 🎨 Material You dynamic theming
- 🌓 Dark/Light mode support
- 📱 Modern Jetpack Compose UI
- 🔄 Background synchronization
- 🔔 Precise prayer time notifications

## 🛠️ Technology Stack

### Core
- 100% Kotlin
- Clean Architecture with MVVM
- Jetpack Compose for UI
- Material Design 3
- Single Activity Architecture

### Android Jetpack
- Navigation Compose
- Room Database
- ViewModel
- Glance for Widgets
- WorkManager
- Hilt for DI

### Asynchronous Operations
- Coroutines
- Flow & StateFlow
- LiveData

### Data & Networking
- Retrofit
- Gson
- Kotlinx Serialization
- Location Services
- Background Services

### UI & Graphics
- Custom Compose Animations
- Vico Charts for Statistics
- Animated Navigation Bar
- Custom Widgets
- Gyroscope Integration

### Other Libraries
- ThreeTenABP for Date/Time
- Accompanist
- Custom Audio Player
- Background Location Updates

## 🏗️ Architecture

The app follows Clean Architecture principles with three main layers:

### 🎨 Presentation Layer
- MVVM Pattern
- Compose UI Components
- ViewModels
- State Management
- Navigation

### 💼 Domain Layer
- Use Cases
- Repository Interfaces
- Domain Models
- Business Logic

### 💾 Data Layer
- Repositories Implementation
- Local Database
- Remote Data Source
- Data Models
- Background Services

## 🔧 Setup

1. Clone the repository
```bash
git clone https://github.com/barmex61/PrayTimes.git
```

2. Open in Android Studio

3. Add required API keys in local.properties:
```properties
MAPS_API_KEY=your_google_maps_api_key
```

4. Build and run the project

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 🐛 Bug Reports

If you find a bug or have a suggestion, please open an issue on GitHub.

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Contact

For questions and feedback:

- GitHub Issues
- Email: koc.fatih.tr.61@gmail.com

## 🌟 Support

If you find this project helpful, please consider giving it a star ⭐️
