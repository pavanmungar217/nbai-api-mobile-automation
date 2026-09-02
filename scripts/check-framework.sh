#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JAVA_ROOTS=("$PROJECT_ROOT/src/main/java" "$PROJECT_ROOT/src/test/java")

fail_on_pattern() {
  local description="$1"
  local pattern="$2"
  if rg --line-number --glob '*.java' "$pattern" "${JAVA_ROOTS[@]}"; then
    echo "Framework check failed: $description" >&2
    exit 1
  fi
}

fail_on_pattern "global REST Assured state is forbidden" 'RestAssured\.(baseURI|port|authentication)[[:space:]]*='
fail_on_pattern "TestNG order dependencies are forbidden" 'dependsOnMethods|dependsOnGroups|priority[[:space:]]*='
fail_on_pattern "arbitrary sleeps are forbidden" 'Thread\.sleep[[:space:]]*\('
fail_on_pattern "uncontrolled REST Assured logging is forbidden" '\.log\(\)\.all\(\)'
if rg --line-number --glob '*.java' 'https?://' "$PROJECT_ROOT/src/main/java"; then
  echo "Framework check failed: absolute endpoint URLs do not belong in main Java source" >&2
  exit 1
fi
if rg --line-number 'public[[:space:]]+record' \
  "$PROJECT_ROOT/src/main/java/com/nbai/automation/api/booker/model"; then
  echo "Framework check failed: API models must be Lombok POJOs, not Java records" >&2
  exit 1
fi
fail_on_pattern "known sample credentials must not be committed" 'password123|admin[[:space:]]*:[[:space:]]*admin'

required_files=(
  "$PROJECT_ROOT/AGENTS.md"
  "$PROJECT_ROOT/pom.xml"
  "$PROJECT_ROOT/src/test/resources/config/default.properties"
  "$PROJECT_ROOT/src/test/resources/logback-test.xml"
  "$PROJECT_ROOT/src/test/resources/META-INF/services/org.testng.ITestNGListener"
  "$PROJECT_ROOT/src/test/resources/suites/api-testng.xml"
  "$PROJECT_ROOT/src/main/java/com/nbai/automation/core/http/ApiClientFactory.java"
  "$PROJECT_ROOT/src/main/java/com/nbai/automation/api/booker/service/BookerServices.java"
  "$PROJECT_ROOT/src/main/java/com/nbai/automation/api/booker/service/BookerServicesFactory.java"
  "$PROJECT_ROOT/src/main/java/com/nbai/automation/api/booker/model/request/BookingRequest.java"
  "$PROJECT_ROOT/src/main/java/com/nbai/automation/api/booker/model/response/BookingResponse.java"
)

for required_file in "${required_files[@]}"; do
  if [[ ! -f "$required_file" ]]; then
    echo "Framework check failed: missing $required_file" >&2
    exit 1
  fi
done

echo "Framework static checks passed."
