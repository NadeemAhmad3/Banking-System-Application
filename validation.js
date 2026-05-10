// validation.js
// Post-migration validation using Prisma Client queries.

const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

async function validate() {
  console.log('--- Post-Migration Validation Queries ---\n');

  // V1: Total migrated row count — must match valid CSV rows (9)
  const v1 = await prisma.appointment.count();
  console.log(`V1 — Migrated row count     : ${v1}  (expected: 9)`);

  // V2: NULL datetime check — must be 0 (T1 must have parsed every date)
  const v2Raw = await prisma.$queryRaw`SELECT COUNT(*) AS null_dates FROM Appointment WHERE appt_datetime IS NULL`;
  const v2 = Number(v2Raw[0].null_dates);
  console.log(`V2 — Rows with NULL datetime : ${v2}  (expected: 0)`);

  // V3: Distinct status values — must only contain valid codes from appt_status_ref
  const v3Raw = await prisma.appointment.findMany({
    distinct: ['status'],
    select: { status: true },
    orderBy: { status: 'asc' },
  });
  const v3 = v3Raw.map(r => r.status).join(', ');
  console.log(`V3 — Distinct status codes  : [${v3}]  (expected: C, H, P, R, X)`);

  // V4: Orphan check — uses lowercase table name matching Prisma's default mapping
  // Finds appointments whose patient_id has no matching record in Patient table
  const v4Raw = await prisma.$queryRaw`
    SELECT COUNT(*) AS orphans
    FROM appointment a
    LEFT JOIN patient p ON a.patient_id = p.patient_id
    WHERE p.patient_id IS NULL
  `;
  const v4 = Number(v4Raw[0].orphans);
  console.log(`V4 — Orphan appointments    : ${v4}  (expected: 0)`);

  console.log('\n--- Validation Complete ---');
  await prisma.$disconnect();
}

validate().catch(async (e) => {
  console.error('Validation error:', e);
  await prisma.$disconnect();
  process.exit(1);
});