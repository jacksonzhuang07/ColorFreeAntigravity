# Implementation Plan - ColorFree Android App (V7)

V7 introduces randomized friction to prevent users from getting used to one type of challenge.

## User Review Required

> [!NOTE]
> **Randomization**: Every time you try to unlock color or change settings, a random challenge will appear.
> **Challenges**: Math, Rotate, Shake, Scroll.

## Proposed Changes

### [NEW] Friction Modules
#### [NEW] [ShakeChallengeDialog](file:///c:/Users/jray/OneDrive - Omnitrans Inc/Desktop/ColorFree/app/src/main/java/com/example/colorfree/ShakeChallengeDialog.java)
*   Uses `Sensor.TYPE_ACCELEROMETER`.
*   Detects `g-force > 12`.
*   Requires 50 shakes.

#### [NEW] [ScrollChallengeDialog](file:///c:/Users/jray/OneDrive - Omnitrans Inc/Desktop/ColorFree/app/src/main/java/com/example/colorfree/ScrollChallengeDialog.java)
*   A `ScrollView` with a hidden "Unlock" button at the very bottom of a 10,000dp view.
*   Text at top: "Scroll to the bottom to unlock."

### [MODIFY] Logic
#### [MODIFY] [MainActivity](file:///c:/Users/jray/OneDrive - Omnitrans Inc/Desktop/ColorFree/app/src/main/java/com/example/colorfree/MainActivity.java)
*   Refactor `showUnlockDialog` to call `ChallengeRouter`.

#### [NEW] [ChallengeRouter](file:///c:/Users/jray/OneDrive - Omnitrans Inc/Desktop/ColorFree/app/src/main/java/com/example/colorfree/ChallengeRouter.java)
*   `showRandomChallenge(context, successCallback, failCallback)`
*   Randomly selects specific dialog.

## Verification Plan

### Manual Verification
*   **Rotation**: Verify existing.
*   **Shake**: Create dialog, shake device, verify counter decrements.
*   **Scroll**: Verify "Unlock" button is off-screen initially. Scroll down, click it, verify success.
*   **Math**: Verify existing.
*   **Randomness**: Clean install, try unlocking 5 times. Should see different challenges.
