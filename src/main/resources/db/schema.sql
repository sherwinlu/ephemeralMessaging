CREATE TABLE IF NOT EXISTS `fitnesspal`.`message` (
  `id` BIGINT NOT NULL,
  `username` VARCHAR(255) NOT NULL,
  `text` TEXT NOT NULL,
  `expirationDate` TIMESTAMP NOT NULL,
  UNIQUE INDEX `id_UNIQUE` (`id` ASC, `username` ASC))
ENGINE = InnoDB