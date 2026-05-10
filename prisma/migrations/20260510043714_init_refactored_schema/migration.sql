-- CreateTable
CREATE TABLE `Patient` (
    `patient_id` INTEGER NOT NULL AUTO_INCREMENT,
    `full_name` VARCHAR(255) NOT NULL,
    `dob` DATE NOT NULL,
    `sex` ENUM('M', 'F', 'NB') NOT NULL,
    `registered_doctor_id` INTEGER NULL,
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `updated_at` DATETIME(3) NOT NULL,

    PRIMARY KEY (`patient_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `PatientPhone` (
    `phone_id` INTEGER NOT NULL AUTO_INCREMENT,
    `patient_id` INTEGER NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `phone_type` ENUM('mobile', 'home', 'work', 'emergency') NOT NULL DEFAULT 'mobile',

    PRIMARY KEY (`phone_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `PatientAddress` (
    `address_id` INTEGER NOT NULL AUTO_INCREMENT,
    `patient_id` INTEGER NOT NULL,
    `addr_line1` VARCHAR(255) NOT NULL,
    `addr_line2` VARCHAR(255) NULL,
    `city` VARCHAR(100) NOT NULL,
    `address_type` ENUM('home', 'work', 'other') NOT NULL DEFAULT 'home',

    PRIMARY KEY (`address_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `PatientClinicalNote` (
    `note_id` INTEGER NOT NULL AUTO_INCREMENT,
    `patient_id` INTEGER NOT NULL,
    `note_type` ENUM('allergy', 'clinical', 'administrative') NOT NULL,
    `note_text` TEXT NOT NULL,
    `recorded_by` INTEGER NULL,
    `recorded_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    PRIMARY KEY (`note_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `Doctor` (
    `doctor_id` INTEGER NOT NULL,
    `full_name` VARCHAR(255) NOT NULL,
    `speciality` VARCHAR(255) NOT NULL,
    `contact_no` VARCHAR(20) NOT NULL,
    `join_date` DATE NOT NULL,
    `salary_monthly` DECIMAL(10, 2) NOT NULL,
    `is_active` CHAR(1) NOT NULL,

    PRIMARY KEY (`doctor_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `ApptStatusRef` (
    `status_code` CHAR(1) NOT NULL,
    `description` VARCHAR(50) NOT NULL,

    PRIMARY KEY (`status_code`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `Appointment` (
    `appt_id` INTEGER NOT NULL,
    `patient_id` INTEGER NOT NULL,
    `doc_id` INTEGER NOT NULL,
    `appt_datetime` DATETIME(3) NOT NULL,
    `status` CHAR(1) NOT NULL,
    `fee` DECIMAL(10, 2) NOT NULL,
    `discount` DECIMAL(10, 2) NOT NULL,
    `room_number` INTEGER NOT NULL,
    `building_block` VARCHAR(50) NOT NULL,
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `updated_at` DATETIME(3) NOT NULL,

    PRIMARY KEY (`appt_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `Billing` (
    `bill_no` VARCHAR(50) NOT NULL,
    `pid` INTEGER NOT NULL,
    `patient_name` VARCHAR(255) NOT NULL,
    `services` TEXT NOT NULL,
    `service_cost` DECIMAL(10, 2) NOT NULL,
    `tax_percent` DECIMAL(5, 2) NOT NULL,
    `paid` DECIMAL(10, 2) NOT NULL,
    `created_at` DATETIME(3) NOT NULL,
    `created_by_user` VARCHAR(255) NOT NULL,

    PRIMARY KEY (`bill_no`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- AddForeignKey
ALTER TABLE `Patient` ADD CONSTRAINT `Patient_registered_doctor_id_fkey` FOREIGN KEY (`registered_doctor_id`) REFERENCES `Doctor`(`doctor_id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `PatientPhone` ADD CONSTRAINT `PatientPhone_patient_id_fkey` FOREIGN KEY (`patient_id`) REFERENCES `Patient`(`patient_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `PatientAddress` ADD CONSTRAINT `PatientAddress_patient_id_fkey` FOREIGN KEY (`patient_id`) REFERENCES `Patient`(`patient_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `PatientClinicalNote` ADD CONSTRAINT `PatientClinicalNote_patient_id_fkey` FOREIGN KEY (`patient_id`) REFERENCES `Patient`(`patient_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `Appointment` ADD CONSTRAINT `Appointment_patient_id_fkey` FOREIGN KEY (`patient_id`) REFERENCES `Patient`(`patient_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `Appointment` ADD CONSTRAINT `Appointment_doc_id_fkey` FOREIGN KEY (`doc_id`) REFERENCES `Doctor`(`doctor_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `Appointment` ADD CONSTRAINT `Appointment_status_fkey` FOREIGN KEY (`status`) REFERENCES `ApptStatusRef`(`status_code`) ON DELETE RESTRICT ON UPDATE CASCADE;
