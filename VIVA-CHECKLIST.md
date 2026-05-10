# Software Re-Engineering Project — QUICK REFERENCE & SUBMISSION GUIDE

**Project Status**: ✅ COMPLETE (All 7 Parts)  
**Total Marks**: 100  
**Date Completed**: May 10, 2026  

---

## 🎯 VIVA CHECKLIST — Both Members MUST Know

### Part A–D (Java Code Analysis)
- [ ] SonarQube dashboard running at **http://localhost:9000**
  - Project: banking-system-application
  - Can show: LOC, Code Smells, Cyclomatic Complexity, Maintainability Rating
  
- [ ] **5 Code Smell Categories identified** with file:line references
  - [ ] Category 1: Bloaters (Long Method, Large Class, etc.)
  - [ ] Category 2: OO Abusers (Switch Statements, etc.)
  - [ ] Category 3: Change Preventors (Divergent Change, etc.)
  - [ ] Category 4: Dispensables (Duplicate Code, Comments, etc.)
  - [ ] Category 5: Couplers (Feature Envy, Message Chains, etc.)

- [ ] **Dependency mapping** for 6 classes
  - Can explain: Ca (afferent coupling), Ce (efferent coupling), Instability formula
  - Have dependency graph (Draw.io/Graphviz)

- [ ] **Technical Debt** identified (3 items)
  - Can point to code location
  - Know: Type, Intentional?, Prudent/Reckless, Remediation cost

- [ ] **Method execution trace** (Python Tutor)
  - Can load method and trace through execution
  - Variable values at each step
  - Control flow taken and why

- [ ] **Control Flow Graph** of selected method
  - Know Cyclomatic Complexity calculation
  - Can highlight execution path from trace
  - Propose 1 change to reduce CC

### Part E–G (Database)
- [ ] **Database loaded** and accessible
  - MySQL running: `mysql -u root -p healthbridge`
  - Can show legacy schema with all data smells

- [ ] **15+ Data Smells identified** (Part E)
  - [ ] Redundancy (Duplicate Data, Derived Data)
  - [ ] Structural (Unnormalized, Overloaded Columns, Non-Atomic Fields)
  - [ ] Naming (Inconsistent, Ambiguous)
  - [ ] Integrity (Missing Keys/Constraints)
  - [ ] Data Type (Type Optimization, Misused Boolean)
  - [ ] Semantic (Magic Values, Inconsistent Units)
  - [ ] Security & Lifecycle (No Audit Trail, Unencrypted)

- [ ] **Normalisation to 3NF** (Part F)
  - Show original schema violations (1NF, 2NF, 3NF)
  - Display normalised tables:
    - patients, patient_phones, patient_addresses
    - doctors, appointments, appt_status_ref
  - Prisma schema in schema.prisma reflects normalisation

- [ ] **5 Refactoring Scripts** executable
  - R1: Derived data view (billing)
  - R2: Status reference table (appointments)
  - R3: Naming standardisation (doctors)
  - R4: Add PK/FK constraints (with backfill)
  - R5: Add audit trail (created_at, updated_at)

- [ ] **Migration Script** executable (Part G)
  - Run: `node migration_etl.js`
  - T1: Convert appt_date 'DD/MM/YYYY HH:MM' → DATETIME ✅
  - T2: Split room 'Room 3 Block B' → room_number + building_block ✅
  - T3: Omit patient_nm, patient_ph, doc_name (use FK lookups) ✅
  - T4: Validate status ∈ {P,C,X,H,R}; skip invalid ✅
  - Expected output: 9 rows inserted, 1 row skipped (invalid status 'Z')

- [ ] **Validation Queries** passing (Part G)
  - [ ] V1: Row count matches (9 valid rows)
  - [ ] V2: No NULL datetimes (0)
  - [ ] V3: Only valid statuses (P,C,X,H,R)
  - [ ] V4: No orphan appointments (0)

- [ ] **Prisma Studio** accessible
  - Can browse: patients, doctors, appointments, ApptStatusRef tables
  - Show relationships (FKs) working

---

## 📋 SUBMISSION REQUIREMENTS

### 1. PDF Report (Single Document)
**Parts A–G with sections clearly labeled and paginated**

Each screenshot must have:
- Clear figure caption (e.g., "Figure 3.1: SonarQube Code Smells Dashboard")
- 2–3 sentence explanation IMMEDIATELY below
- **Screenshots without explanations receive ZERO marks**

**Required Screenshots**:
- [ ] Part A: GitHub repo page, SonarQube dashboard, tool versions
- [ ] Part B: SonarQube metrics dashboard, code locations of 5 smell categories, before/after refactoring code
- [ ] Part C: Dependency graph visualization, coupling analysis table
- [ ] Part D: Python Tutor execution trace (with variables visible), Control Flow Graph, AST Explorer
- [ ] Parts E–F: Prisma schema.prisma, normalisation diagrams, migration logs
- [ ] Part G: Migration script output, validation query results

### 2. GitHub Repository
**Must contain**:
```
Banking-System-Application/
├── README.md (Quick start guide)
├── README-DETAILED.md (Technical reference)
├── Bank System Application/ (Java project)
├── Database/ (Legacy schema SQL)
├── prisma/ (Normalised schema)
├── legacy_appointments.csv (Migration source)
├── migration_etl.js (ETL script)
├── validation.js (Validation queries)
├── sonar-project.properties
└── .env (Database config)
```

**README must have**:
- [ ] Commands to start SonarQube: `docker run ... sonarqube:lts`
- [ ] Commands to compile Java: `javac -d out ...`
- [ ] Commands to run SonarScanner: `sonar-scanner ...`
- [ ] Commands to load hospital schema: `mysql ... < Database/database\ scheme.sql`
- [ ] Commands to run migrations: `npx prisma migrate dev`
- [ ] Commands to run ETL: `node migration_etl.js`
- [ ] Commands to validate: `npm run validate-migration` or query commands
- [ ] Clear folder structure explanation
- [ ] Group member names and roll numbers

### 3. Executable SQL Scripts
**All scripts MUST run without modification**:
- [ ] Database/database\ scheme.sql (Legacy schema — loads without errors)
- [ ] prisma/migrations/[timestamp]_init_refactored_schema/migration.sql
- [ ] All 5 refactoring scripts (R1–R5) in separate files or documented section

**Test each script**:
```bash
mysql -u root -p healthbridge < script.sql
# Should complete without errors
```

---

## 📊 MARKS BREAKDOWN & Coverage

| Part | Max Marks | Key Deliverables | Status |
|------|-----------|-----------------|--------|
| **A1** | 3 | Java project description (2 para), LOC/class count, screenshot, candidate explanation | ✅ |
| **A2** | 5 | All 8 tools installed & verified with version output | ✅ |
| **B1** | 5 | SonarQube analysis, sonar-project.properties, metrics extraction with interpretation | ✅ |
| **B2** | 16 | 5 categories × 2+ smells each = 10+ smells with file:line, evidence, treatment | ✅ |
| **B3** | 3 | Smell interaction analysis (250+ words), prioritisation strategy | ✅ |
| **B4** | 3 | Before/after code, refactoring applied, behavior unchanged | ✅ |
| **C1** | 7 | 6 classes with Ca/Ce/I, dependency graph, circular dep analysis | ✅ |
| **C2** | 8 | 3 debt items, classification, remediation cost, debt ratio & interpretation | ✅ |
| **D1** | 4 | Method selected, execution trace table, Python Tutor screenshot (variables visible) | ✅ |
| **D2** | 3 | CFG diagram, CC calculation, independent paths, reduction strategy | ✅ |
| **D3** | 3 | AST Explorer screenshot (3+ levels), annotated node types, use cases | ✅ |
| **E2** | 9 | 10 distinct data smells, 5 categories, evidence, risk, fix for each | ✅ |
| **E3** | 6 | 4 smells prioritised with hospital risk scenarios, ranking justification | ✅ |
| **F1** | 6 | 1NF/2NF/3NF violation analysis, normalised CREATE TABLE statements (patients, phones, etc.) | ✅ |
| **F2** | 6 | 5 refactoring scripts (R1–R5) with explanations | ✅ |
| **F3** | 3 | Impact summary for each refactoring, effort vs benefit analysis | ✅ |
| **G2** | 3 | Migration plan (source, target, transformations, validation, downtime) | ✅ |
| **G3** | 5 | Complete ETL script (T1–T4), migration log screenshot | ✅ |
| **G4** | 2 | Validation queries (V1–V4) with results, all queries passing | ✅ |
| **TOTAL** | **100** | All parts complete and ready for viva | ✅ |

---

## 🔧 FINAL EXECUTION CHECKLIST (Day Before Viva)

### 1 Day Before Viva
```bash
# Compile everything
cd "Bank System Application"
javac -d ../out $(find src -name "*.java")
cd ..

# Verify tools
sonar-scanner --version
docker --version
mysql --version
node --version
npx prisma --version

# Test database
mysql -u root -p -e "USE healthbridge; SHOW TABLES;"
```

### 1 Hour Before Viva
```bash
# Start SonarQube
docker run -d --name sonarqube \
  -p 9000:9000 \
  -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLED=true \
  sonarqube:lts

# Wait 60 seconds, then verify
curl -u admin:SRE-PROJECT-2026 http://localhost:9000/api/system/status

# Run SonarScanner analysis
sonar-scanner

# Check database is loaded
mysql -u root -p < "Database/database scheme.sql"

# Verify Prisma setup
npx prisma generate
npx prisma migrate deploy

# Run migration
node migration_etl.js

# Validate migration
npm run validate-migration  # Or run SQL queries manually
```

### During Viva
- [ ] Both members present
- [ ] SonarQube dashboard visible & responsive
- [ ] Database accessible (show SHOW TABLES)
- [ ] Migration script output visible
- [ ] Code files openable in editor
- [ ] Can point to exact file:line of any smell
- [ ] Can explain root cause and refactoring for any smell
- [ ] Can run validation queries and show results

---

## 🚨 COMMON MISTAKES TO AVOID

| Mistake | Impact | Prevention |
|---------|--------|-----------|
| Copied smell interpretation from slides | ZERO MARKS for that section | Write interpretation specific to your code (mention class names, line numbers) |
| Generic definitions without project context | No marks | Explain WHAT makes YOUR code smelly, not textbook definition |
| Screenshots without captions/explanations | Half marks | Every screenshot must have figure number, caption, and 2–3 sentence explanation |
| SQL scripts with syntax errors | ZERO MARKS for that section | Test every script: `mysql -u root -p < script.sql` before submission |
| Placeholders in tables | No marks for that row | Complete EVERY cell — use actual values from your project |
| Tools not running during viva | Verification impossible | Start tools 1 hour before; test all connections |
| Group members can't answer about their own work | Verification of understanding fails | Both members must understand ALL parts |
| Missing file:line references for code locations | Can't verify claims | Always include: FileName.java:LineNo |
| Generic Technical Debt identification | No marks | Must cite actual SonarQube values (minutes/hours) |
| Incomplete normalisation (missing tables/FKs) | Marks deducted | Include: patients, phones, addresses, status_ref, constraints |

---

## 📬 FINAL SUBMISSION

**To Submit**:
1. **PDF Report** (A–G with all screenshots & explanations)
2. **GitHub Repository URL** (with all code, README, SQL scripts)
3. **Email to**: engrnadeem25@gmail.com

**Email Subject**: `[SRE Project] Group [X] — Software Re-Engineering Report`

**Email Body**:
```
Subject: [SRE Project] Group [Number] — Software Re-Engineering Report

Dear Sir/Madam,

Please find attached our Software Re-Engineering project submission for the 
course BS Software Engineering, May 2026.

Project Overview:
- Java Project: Bank System Application (2,500+ LOC, 30+ classes)
- Database: HealthBridge Hospital Management System (15+ data smells)
- All 7 parts (A–G) completed with 100 marks worth of deliverables

Submission Contents:
1. PDF Report (100+ pages) — All parts A–G with screenshots & explanations
2. GitHub Repository — Complete source code, Prisma schema, migration scripts
3. Executable SQL scripts — All refactoring and migration scripts tested

For Viva:
- SonarQube: Accessible at http://localhost:9000
- Database: MySQL with legacy + normalised schemas
- Migration: ETL script (node migration_etl.js) ready to run
- All tools verified and running

Both group members will be present for viva demonstration.

Submitted by:
[Member 1 Name] — Roll: [Roll 1]
[Member 2 Name] — Roll: [Roll 2]

Date: May 10, 2026
Repository: [GitHub URL]

Best regards,
[Group Members]
```

---

## 📚 QUICK REFERENCE: SMELL CATEGORIES & TREATMENTS

### Bloaters → Extract Class / Extract Method
### OO Abusers → Polymorphism / Strategy Pattern
### Change Preventors → Extract Class (SRP)
### Dispensables → Remove Duplication / Remove Comments / Move Method
### Couplers → Move Method / Hide Delegate / Extract Class

### Data Smells → Normalisation (1NF/2NF/3NF)
### Refactorings → R1–R5 (Derived data, Status ref, Naming, PK/FK, Audit trail)
### ETL → T1–T4 (Date conversion, Field split, Omit duplicates, Validate codes)
### Validation → V1–V4 (Row count, NULL check, Status validation, FK integrity)

---

**Last Updated**: May 10, 2026  
**Project Status**: ✅ COMPLETE & READY FOR VIVA  
**Estimated Viva Duration**: 30–45 minutes (both members)

For detailed technical information, see: **README-DETAILED.md**  
For quick start instructions, see: **README.md**
