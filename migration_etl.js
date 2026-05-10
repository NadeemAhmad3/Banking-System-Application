// migration_etl.js
// Migrates legacy CSV appointment data into the refactored schema using Prisma ORM.
// Implements all four required transformations (T1-T4).

const fs = require('fs');
const csv = require('csv-parser');
const { PrismaClient, Sex } = require('@prisma/client');

const prisma = new PrismaClient({});

// T4: Valid status codes as defined in appt_status_ref (R2 fix from Part F)
const VALID_STATUSES = new Set(['P', 'C', 'X', 'H', 'R']);

// T1: Converts 'DD/MM/YYYY HH:MM' string into a proper JavaScript Date object
// Fixes the Data Type smell — appt_date was stored as plain VARCHAR text
function parseApptDate(raw) {
  const [datePart, timePart] = raw.trim().split(' ');
  const [day, month, year] = datePart.split('/').map(Number);
  const [hours, minutes] = timePart.split(':').map(Number);
  return new Date(Date.UTC(year, month - 1, day, hours, minutes));
}

// T2: Splits 'Room 3 Block B' into { room_number: 3, building_block: 'Block B' }
// Fixes the Non-Atomic Fields smell — two distinct facts in one column
function splitRoom(raw) {
  const parts = raw.trim().split(' ');
  const room_number = parseInt(parts[1], 10);
  const building_block = parts.slice(2).join(' ');
  return { room_number, building_block };
}

async function migrate() {
  console.log('--- HealthBridge ETL Migration Starting ---');

  // Seed the status reference table (R2 from Part F)
  await prisma.apptStatusRef.createMany({
    data: [
      { status_code: 'P', description: 'Pending' },
      { status_code: 'C', description: 'Completed' },
      { status_code: 'X', description: 'Cancelled' },
      { status_code: 'H', description: 'On Hold' },
      { status_code: 'R', description: 'Rescheduled' },
    ],
    skipDuplicates: true,
  });
  console.log('Status reference table seeded.');

  // Seed patients using the Sex enum from your Part F normalised schema
  // sex field uses enum values M / F / NB — NOT plain char strings
  await prisma.patient.createMany({
    data: [
      {
        patient_id: 5,
        full_name: 'Ali Hassan',
        dob: new Date('1990-01-01'),
        sex: Sex.M,                   // enum value, not 'M' string
        registered_doctor_id: 12,
      },
      {
        patient_id: 8,
        full_name: 'Sara Malik',
        dob: new Date('1985-05-15'),
        sex: Sex.F,
        registered_doctor_id: 7,
      },
      {
        patient_id: 21,
        full_name: 'Hina Iqbal',
        dob: new Date('1992-08-22'),
        sex: Sex.F,
        registered_doctor_id: 7,
      },
    ],
    skipDuplicates: true,
  });

  // Seed doctors with ALL required fields from Part F R3 renamed columns
  // Omitting join_date / contact_no / salary_monthly / is_active would
  // cause a Prisma validation error since they are NOT NULL in the schema
  await prisma.doctor.createMany({
    data: [
      {
        doctor_id:      12,
        full_name:      'Dr. Kamran Raza',
        speciality:     'General',
        contact_no:     '0300-0000001',
        join_date:      new Date('2010-01-01'),
        salary_monthly: '85000.00',   // string keeps Decimal precision (Part F FLOAT fix)
        is_active:      'Y',
      },
      {
        doctor_id:      7,
        full_name:      'Dr. Ayesha Noor',
        speciality:     'Cardiology',
        contact_no:     '0300-0000002',
        join_date:      new Date('2012-06-15'),
        salary_monthly: '120000.00',
        is_active:      'Y',
      },
    ],
    skipDuplicates: true,
  });
  console.log('Patient and Doctor seed data inserted.');

  // Collect all rows from CSV before inserting
  // Avoids race conditions between the async stream and DB writes
  const validRows = [];
  const skipped   = [];

  await new Promise((resolve, reject) => {
    fs.createReadStream('./legacy_appointments.csv')
      .pipe(csv())
      .on('data', (row) => {
        // T4: Validate status — skip and log any unknown code (R2 fix from Part F)
        if (!VALID_STATUSES.has(row.status)) {
          console.warn(`  [SKIPPED] appt_id=${row.appt_id} — invalid status: '${row.status}'`);
          skipped.push(row.appt_id);
          return;
        }

        const appt_datetime              = parseApptDate(row.appt_date);   // T1
        const { room_number, building_block } = splitRoom(row.room);       // T2

        // T3: patient_nm, patient_ph, doc_name intentionally omitted
        // These are now derived via FK joins — keeping them would re-introduce
        // the Duplicate Data smell that Part F normalisation removed
        validRows.push({
          appt_id:        parseInt(row.appt_id, 10),
          patient_id:     parseInt(row.patient_id, 10),
          doc_id:         parseInt(row.doc_id, 10),
          appt_datetime,
          status:         row.status,
          fee:            row.fee.toString(),      // string preserves Decimal precision
          discount:       row.discount.toString(), // fixes Part E FLOAT smell for money
          room_number,
          building_block,
        });
      })
      .on('end', resolve)
      .on('error', reject);
  });

  console.log(`CSV read complete. ${validRows.length} valid, ${skipped.length} skipped.`);

  const result = await prisma.appointment.createMany({
    data: validRows,
    skipDuplicates: true,
  });

  console.log(`\nMigration complete.`);
  console.log(`  Rows inserted : ${result.count}`);
  console.log(`  Rows skipped  : ${skipped.length} (invalid codes: [${skipped.join(', ')}])`);
  console.log('--- ETL Migration Finished ---');

  await prisma.$disconnect();
}

migrate().catch(async (e) => {
  console.error('Migration failed:', e);
  await prisma.$disconnect();
  process.exit(1);
});