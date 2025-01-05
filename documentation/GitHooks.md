# Git Hooks

This project has some Git hooks included inside the [git-hooks](/git-hooks) folder. 

## **Usage Instructions**

1. **Install Git Hooks**:
   - Run the `clean` task to automatically install or update hooks:
     ```bash
     ./gradlew clean
     ```

2. **Commit-Msg Hook**:
   - Ensures commit messages include an issue number (e.g., `#123` or `[ #123 ]`).
   - Blocks commits with invalid messages.

---

## **Examples**

### Valid Commit Messages:
- `Fix login issue #123`
- `[ #456 ] Refactor module`

### Invalid Commit Messages:
- `Fix login issue` _(No issue number)._
- `Updated README 123` _(Missing `#` prefix)._

## **Why These settings?**

1. **Improving Traceability**:
   - The `commit-msg` hook enforces commit message standards, making it easier to link commits to specific issues.

2. **Automating Hook Installation**:
   - Integrating hook installation with the `clean` task reduces manual steps and ensures hooks are always up-to-date.

---

## **Directory Structure**

```plaintext
project-root/
├── .git/
│   ├── hooks/
│       ├── pre-commit
│       ├── pre-push
│       ├── commit-msg
├── build.gradle.kts
├── git-hooks/
│   ├── pre-commit-unix.sh
│   ├── pre-commit-windows.sh
│   ├── pre-push-unix.sh
│   ├── pre-push-windows.sh
│   ├── commit-msg-unix.sh
│   ├── commit-msg-windows.sh
