# ─── MultiBank QA Framework – Test Runner ────────────────────────────────────
# Multi-stage build:
#   Stage 1 (deps)   – download Maven dependencies (cached layer)
#   Stage 2 (runner) – final lean image with just what's needed to run tests

# ── Stage 1: dependency cache ─────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-11 AS deps

WORKDIR /app

# Copy only pom.xml first — lets Docker cache the dependency layer
# and skip re-downloading on source-only changes
COPY pom.xml .
RUN mvn dependency:go-offline --no-transfer-progress -q

# ── Stage 2: test runner ──────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-11 AS runner

LABEL maintainer="QA Team"
LABEL description="MultiBank QA Automation Framework"

WORKDIR /app

# Carry over the cached local repository from Stage 1
COPY --from=deps /root/.m2 /root/.m2

# Copy full project source
COPY . .

# Create output directories
RUN mkdir -p target/screenshots \
             target/extent-reports \
             target/allure-results \
             target/logs

# Environment defaults (overridable at runtime)
ENV BROWSER=chrome
ENV HEADLESS=true
ENV SUITE=regression

# Default command: run regression suite
# Override at container run time:
#   docker run multibank-tests mvn test -Psmoke -Dbrowser=firefox
CMD ["mvn", "test", \
     "-Pregression", \
     "-Dbrowser=chrome", \
     "-Dheadless=true", \
     "--no-transfer-progress"]
