# Google Play Families Program — Kids123

## Designed for Families

Kids123 is designed for children ages **2–5** and targets the [Google Play Designed for Families](https://support.google.com/googleplay/android-developer/answer/9893335) program.

### Age-appropriate content

- Numbers 1–20 with colorful dot-counting visuals; no user-generated content.
- LEARN, TRACE, and QUIZ modes contain **no third-party advertising**.

### COPPA compliance

- No personal information is collected from children without verifiable parental consent.
- Analytics and ads are gated behind the consent/onboarding flow in the app shell.
- Learning screens (`GameScreen` / `Kids123Board`) do **not** display ads during active play.

### Advertising policy

- `AdBanner` may remain in the app shell (home, settings, post-level dialogs) for monetization of non-learning surfaces.
- **Recommendation:** Implement a **parent gate** (math problem or hold-to-unlock) before showing any interstitial or rewarded ad, especially when navigating away from learning content.
- Do not show ads on LEARN / TRACE / QUIZ screens.

### Privacy

- See [PRIVACY_POLICY.md](PRIVACY_POLICY.md) for data handling details.
- Replace the stub `google-services.json` with your Firebase project before release.

### Store listing

- Set **Target audience** to ages 5 and under in Play Console.
- Enable **Designed for Families** and complete the Families Policy questionnaire.
- Declare ads and COPPA status accurately.
