# Task 9: Selenium browser testing

## What the tests cover

`SeleniumBrowserTests` drives the real portal in headless Chrome. It checks the lookup form, a successful fictional booking lookup, a missing booking message, assigning an available seat and checking in, and rejection of an occupied seat. The check-in test verifies the visible demo boarding pass warning. The occupied-seat test also checks that the persisted booking remains unchanged after the rejection.

Each test gets fresh fictional `DEMOSEL` bookings in a separate in-memory H2 database. This keeps the tests independent of each other and of the API tests' sample data. Browser tests use the application's real HTTP routes and keep the existing security configuration intact.

## Run locally on Windows

Use Java 21, install Google Chrome, and run these commands from the repository root:

```powershell
.\mvnw.cmd clean verify
.\mvnw.cmd -Pselenium verify
```

The first command runs the regular test suite. The `selenium` Maven profile runs the browser tests. Selenium Manager, included with Selenium, locates a compatible ChromeDriver and caches it under the current user's Selenium cache. If a matching driver is not cached, the first browser run needs internet access to download it. No separate driver install is needed for a developer machine with network access. Set `CHROME_BINARY` when Chrome is installed outside its standard location.

Selenium JUnit XML and text reports are saved under `target/selenium-output/reports/`. Failed browser tests save screenshots under `target/selenium-output/screenshots/`. Maven's normal reports remain under `target/surefire-reports/`.

## Jenkins prerequisites and status

The Jenkinsfile has a separate `Browser Tests` stage after unit tests and JAR archiving. It expects the configured `JDK21` Jenkins tool, Chrome at `C:\Program Files\Google\Chrome\Application\chrome.exe`, the Jenkins JUnit publisher, and network access for Selenium Manager to download ChromeDriver if the Jenkins service account has no cached copy. The test process prints its Windows identity, Java location/version, and Chrome version to the build log. Browser reports are published as JUnit results and failure screenshots are archived.

The local Jenkins Windows service is configured to run as `LocalSystem`. It does not share the interactive user's Selenium cache. Its browser driver will be cached separately when Selenium Manager runs under the service account. The Jenkins HTTP API returned 403 during inspection, so the active job configuration and a run of this updated Jenkinsfile have not been verified. These local changes must be committed and pushed by the project owner before the GitHub-backed job can use them; no commit or push was performed.

## Local verification evidence

- `mvnw.cmd -B clean verify`: 20 regular tests, 0 failures, 0 errors, 0 skipped; `BUILD SUCCESS`.
- `mvnw.cmd -B -Pselenium verify`: 5 Selenium tests, 0 failures, 0 errors, 0 skipped; `BUILD SUCCESS`.
- Across both commands: 25 tests, 0 failures, 0 errors, 0 skipped.
- Local browser: Chrome 155.0.8059.85. Selenium Manager downloaded and used ChromeDriver 155.0.8059.84.

