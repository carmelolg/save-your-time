# Contributing to Save Your Time

## Running Tests Locally

```bash
./gradlew test
```

## Code Coverage

This project uses [Kover](https://github.com/Kotlin/kotlinx-kover) for code coverage measurement.

### Generate coverage reports locally

**HTML report** (human-readable):
```bash
./gradlew koverHtmlReport
```
The report is generated at `app/build/reports/kover/html/index.html`.

**XML report** (machine-readable, useful for CI integrations):
```bash
./gradlew koverXmlReport
```
The report is generated at `app/build/reports/kover/xml/report.xml`.

**Both reports at once:**
```bash
./gradlew koverHtmlReport koverXmlReport
```

### Coverage in CI

The [Test & Coverage](.github/workflows/coverage.yml) workflow runs automatically on every push and pull request to `master`.
It executes the unit test suite, generates HTML and XML coverage reports using Kover, and uploads them as a downloadable artifact named **`coverage-report`** attached to each workflow run.

To find coverage results for a CI run:
1. Navigate to the **Actions** tab of the repository.
2. Select the **Test & Coverage** workflow run of interest.
3. Download the **`coverage-report`** artifact from the run summary.
