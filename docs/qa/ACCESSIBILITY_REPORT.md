# Accessibility Report — v3.0.0

## Automated Checks

| Check | Status |
|-------|--------|
| Biometric button content description | ✅ `AccessibilityAuditTest` |
| Calculator key labels | ⚠️ Visual only — add `contentDescription` per key |
| Bottom nav icons | ⚠️ `contentDescription = tab label` — OK |
| Setup PIN fields | ⚠️ Missing explicit semantics |
| Large font scaling | ⚠️ Manual verification needed |
| TalkBack note editor | ⚠️ Manual verification needed |
| High contrast | ⚠️ Uses Material3 dynamic color — test manually |

## Score

**Accessibility readiness: 65/100**

## Priority Fixes

1. Add `contentDescription` to calculator digit keys
2. Mark note list items with `semantics { contentDescription = title }`
3. Ensure PIN unlock dialog is focus-ordered for TalkBack
4. Run Accessibility Scanner on vault screens before release
