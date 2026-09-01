# Class Reminder by Emmiejay

An Android app that fires an alarm-style notification **15 minutes before**
every scheduled tutoring class, with a screen to add, edit, and delete
classes going forward. It comes pre-loaded with your current weekly
schedule (Ethan, Davian, Mary, Praise, Great, Jeremy, Amelia, Shiro, Moplin,
Sayo/Sope) from your "Maths Made Simple by Emmiejay" timetable.

## How it works

- Each class is stored (day of week, time, student name) in a local
  database on the phone — no internet or account needed.
- For every class, the app schedules an exact alarm for **15 minutes
  before** the class start time, every week, using Android's `AlarmManager`.
- When it fires, you get a high-priority notification with sound:
  *"<Student>'s class starts in 15 minutes (<time>)"*, and the app
  automatically re-arms itself for the following week.
- Alarms survive a phone restart (a boot receiver re-schedules everything).
- Tap **+** to add a new class. Tap any class in the list to edit its time
  or delete it — the alarm updates automatically.

## Getting an installable APK without Android Studio (recommended if you just want the file)

This project includes a ready-made GitHub Actions workflow
(`.github/workflows/build-apk.yml`) that builds the APK for you in the
cloud — you never touch Android Studio.

1. Go to [github.com](https://github.com), log in (or create a free
   account), and click **New repository**. Name it whatever you like
   (e.g. `class-reminder`), keep it **Private** or Public, don't add a
   README, then **Create repository**.
2. On the new repo's page, click **uploading an existing file** (or
   **Add file → Upload files**).
3. Unzip `ClassReminderByEmmiejay-source.zip` on your computer, then drag
   the **contents** of the `ClassReminder` folder (not the folder itself
   — `app`, `.github`, `build.gradle.kts`, `settings.gradle.kts`,
   `gradle.properties`, `gradle/`, `README.md`, all of it) into the GitHub
   upload box, and click **Commit changes**.
4. Click the **Actions** tab at the top of the repo. You should see a
   workflow run called "Build APK" already running (it starts
   automatically on upload) — if not, click **Build APK → Run workflow**.
5. Wait 3–5 minutes for it to finish (green checkmark).
6. Click into that finished run, scroll to **Artifacts**, and download
   **ClassReminderByEmmiejay-debug-apk** — it's a zip containing
   `app-debug.apk`.
7. Transfer that `.apk` to your phone (email it to yourself, WhatsApp,
   Google Drive, USB cable — any way you like), tap it on the phone, and
   allow "install from unknown sources" when prompted. That's it — installed.

Every time you push a change to the repo (e.g. if I hand you an updated
version later), a fresh APK builds automatically.

## Building the app with Android Studio (alternative)

This is the full source code for the app — it needs to be compiled into an
installable APK using **Android Studio** (free, from Google):

1. Install [Android Studio](https://developer.android.com/studio).
2. Unzip this project and open the `ClassReminder` folder in Android Studio
   (`File → Open`).
3. Let it sync (it will download Gradle and dependencies automatically —
   first sync can take a few minutes).
4. Plug in your Android phone via USB (with USB debugging enabled in
   Developer Options), or use an emulator, and press the green ▶ **Run**
   button. This installs the app straight onto the phone.
5. Alternatively, build an installable file via
   `Build → Build Bundle(s) / APK(s) → Build APK(s)`, then copy the
   resulting `app-debug.apk` from `app/build/outputs/apk/debug/` to your
   phone and open it to install (you'll need to allow "install from
   unknown sources" once).

## First run on the phone

The app will ask for two permissions — say **Allow** to both so reminders
work reliably:
- **Notifications** — so it can show the reminder.
- **Alarms & reminders** — so Android lets it wake the phone at the exact
  minute, even in Doze/battery-saver mode.

## Editing the schedule later

Open the app any time, tap a class to change its day/time or delete it,
or tap **+** to add a new student's class. Every change re-schedules the
alarm immediately — no reinstall needed.

## Notes

- Minimum Android version: Android 8.0 (API 26) and up.
- Package name: `com.emmiejay.classreminder`
- If you'd rather not build it yourself, any local Android developer can
  open this project in Android Studio and generate the APK for you in a
  few minutes.
