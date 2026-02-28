# Save Your Time

<div align="center">
  <img src="graphics/logo/LOGO.svg" alt="Save Your Time Logo" width="200"/>
</div>

<div align="center">

[![Coverage](https://img.shields.io/badge/coverage-view%20report-blue?style=flat-square)](app/build/reports/kover/html/index.html)
[![Version](https://img.shields.io/badge/version-1.2.7-brightgreen?style=flat-square)](app/build.gradle.kts)
[![API Level](https://img.shields.io/badge/API-28+-blue?style=flat-square)](app/build.gradle.kts)

</div>

---

## 📱 Overview

**Save Your Time** is an innovative Android application designed to help you monitor and manage your daily app usage. The app provides comprehensive insights into how much time you spend on different applications, helping you maintain healthy digital habits and improve productivity.

### ✨ Key Features

- **📊 Real-Time Monitoring**: Track your app usage in real-time across all installed applications
- **📈 Usage Analytics**: View detailed statistics and trends through interactive charts and tables
- **⏱️ Time Limits**: Set daily time limits for specific apps with customizable alerts
- **🔔 Smart Notifications**: Receive timely notifications when you exceed your set time limits
- **📅 History Dashboard**: Review your usage patterns over time with historical data
- **🎨 Dark Mode Support**: Enjoy a comfortable viewing experience with full dark mode support
- **🌍 Multi-Language Support**: Available in English, Italian, Spanish, Portuguese, French, German, Arabic, and Japanese
- **🔒 Privacy-First**: All your data stays on your device—no data is shared with third parties

---

## 🚀 Getting Started

### Prerequisites

- **Android 9.0 (API 28)** or higher
- **Android Studio** (latest version recommended)
- **Java 17** or higher
- **Gradle 8.x**

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/carmelolagamba/save-your-time.git
   cd save-your-time
   ```

2. **Build the project**:
   ```bash
   ./gradlew build
   ```

3. **Run the app on an emulator or device**:
   ```bash
   ./gradlew installDebug
   ```

---

## 📋 Requirements

The app requires the following Android permissions to function:

- **Usage Stats Access**: Monitor app usage data
- **Exact Alarm Permission**: Schedule precise notifications
- **Notification Permission**: Send usage alerts
- **Battery Optimization Exemption**: Maintain continuous monitoring
- **Foreground Service**: Run monitoring in the background

All permissions are requested at runtime with clear explanations of why they're needed.

---

## 🏗️ Architecture

Save Your Time is built using modern Android development practices:

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Data Binding**: AndroidX Data Binding
- **Database**: Room
- **UI Framework**: AndroidX, Material Design 3
- **Testing**: JUnit, Mockito, Kover

### Project Structure

```
app/src/
├── main/
│   ├── java/it/carmelolagamba/saveyourtime/
│   │   ├── ui/              # UI components and fragments
│   │   ├── service/         # Background services
│   │   ├── persistence/     # Database and data models
│   │   └── ...
│   └── res/                 # Resources (layouts, strings, etc.)
└── test/                    # Unit tests
```

---

## 🧪 Testing & Code Coverage

This project maintains high code quality standards with comprehensive unit testing.

### Running Tests

Run all unit tests:
```bash
./gradlew test
```

### Code Coverage Reports

This project uses **[Kover](https://github.com/Kotlin/kotlinx-kover)** for code coverage measurement.

Generate an HTML report (human-readable):
```bash
./gradlew koverHtmlReport
```
The report will be available at `app/build/reports/kover/html/index.html`.

Generate an XML report (machine-readable, useful for CI):
```bash
./gradlew koverXmlReport
```
The report will be available at `app/build/reports/kover/xml/report.xml`.

Generate both reports at once:
```bash
./gradlew koverHtmlReport koverXmlReport
```

### Coverage in CI

The **[Test & Coverage](.github/workflows/coverage.yml)** workflow runs automatically on every push and pull request to `master`. It:
- Executes the complete unit test suite
- Generates HTML and XML coverage reports
- Uploads coverage reports as downloadable artifacts

To view coverage results:
1. Navigate to the **Actions** tab on GitHub
2. Select the **Test & Coverage** workflow run
3. Download the **`coverage-report`** artifact from the run summary

---

## 🔧 Development Setup

### Prerequisites for Development

- Android Studio 2024.1 or later
- Kotlin plugin (bundled with Android Studio)
- SDK Platform Android 36 (API level 36)
- Android SDK Build-tools 36.0.0

### Build Variants

The project supports multiple build types:

- **Debug**: Full debugging capabilities with minimal optimizations
- **Release**: Optimized for production with code minification

---

## 📚 Features in Detail

### Home Dashboard
The main screen displays a summary of your app usage with:
- Total apps being monitored
- Time spent per application
- Remaining time before reaching limits
- Quick access to settings

### History Dashboard
Track your usage trends over time with:
- Daily usage statistics
- Weekly and monthly trends
- Visual charts showing usage patterns
- Detailed usage breakdown by application

### Control Panel
Manage your monitored apps:
- Add or remove apps from monitoring
- Set daily time limits
- Customize notification preferences
- View real-time app activity

### Preferences
Personalize your experience:
- Configure alert thresholds
- Manage notification settings
- Choose display themes
- Set time range for monitoring

---

## 🤝 Contributing

We welcome contributions from the community! To contribute:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/your-feature`
3. **Make your changes** following the project's code style
4. **Write or update tests** to maintain coverage
5. **Commit your changes**: `git commit -am 'Add your feature'`
6. **Push to the branch**: `git push origin feature/your-feature`
7. **Submit a Pull Request**

### Development Guidelines

- Write clean, readable Kotlin code
- Follow the MVVM architecture pattern
- Add unit tests for all new functionality
- Ensure code coverage remains above 80%
- Update documentation as needed

For detailed contribution guidelines, see [CONTRIBUTING.md](CONTRIBUTING.md).

---

## 📄 License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for full details.

---

## 🔐 Privacy & Data Protection

**Your privacy is our priority.** Save Your Time:

- ✅ Stores all data locally on your device
- ✅ Never shares usage data with external servers
- ✅ Does not collect or transmit personal information
- ✅ Never includes tracking or analytics that leave your device

For detailed privacy information, see [PRIVACY](PRIVACY).

---

## 🐛 Bug Reports & Feature Requests

Found a bug or have a feature request? We'd love to hear from you!

- **Report Issues**: Open an issue on [GitHub Issues](https://github.com/carmelolagamba/save-your-time/issues)
- **Feature Requests**: Submit feature requests with detailed descriptions

---

## 📞 Support

If you encounter any issues or have questions:

1. Check the existing [issues](https://github.com/carmelolagamba/save-your-time/issues)
2. Review the [CONTRIBUTING.md](CONTRIBUTING.md) for development help
3. Open a new issue with a detailed description

---

## 📈 Roadmap

Future planned features:

- [ ] Cloud backup and sync
- [ ] Advanced analytics with ML-based insights
- [ ] Social features and app comparison
- [ ] Parental controls
- [ ] Integration with health apps
- [ ] Customizable widgets

---

## 🙏 Acknowledgments

- Built with ❤️ using Kotlin and Android Jetpack
- Material Design 3 for beautiful UI components
- Kover for comprehensive code coverage reporting
- The Android community for inspiration and support

---

## 📱 Get Started Today

Download Save Your Time from the Google Play Store and take control of your digital habits. Make every moment count!

<div align="center">

**[Download on Google Play Store](https://play.google.com/store/apps/details?id=it.carmelolagamba.saveyourtime)** • **[View on GitHub](https://github.com/carmelolagamba/save-your-time)**

---

*Made with 💚 by Carmelo La Gamba*

</div>

