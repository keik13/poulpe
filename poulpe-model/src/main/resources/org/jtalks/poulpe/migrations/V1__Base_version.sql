ALTER TABLE `BRANCHES` ADD `TYPE` VARCHAR(255) NOT NULL;
ALTER TABLE `SECTIONS` ADD `TYPE` VARCHAR(255) NOT NULL;
ALTER TABLE `SECTIONS` ADD `POSITION` INT(11) NULL DEFAULT NULL;
ALTER TABLE `SECTIONS` ADD `JCOMMUNE_ID` BIGINT(20) NULL DEFAULT NULL;

CREATE TABLE `POULPE_GROUP` (
  `GROUP_ID` BIGINT(20) NOT NULL,
  `BRANCH_ID` BIGINT(20) NULL DEFAULT NULL,
  `POSITION` INT(11) NULL DEFAULT NULL);

 CREATE TABLE `POULPE_BRANCH` (
   `BRANCH_ID` BIGINT(20) NOT NULL);


CREATE  TABLE `BRANCH_USER_REF` (
  `BRANCH_ID` BIGINT(20) NOT NULL,
  `USER_ID` BIGINT(20) NOT NULL,
  CONSTRAINT `FK_BRANCH_USER_REF_USERS_USER_ID`
    FOREIGN KEY (`USER_ID`)
    REFERENCES `USERS` (`ID`),
  CONSTRAINT `FK_BRANCH`
    FOREIGN KEY (`BRANCH_ID`)
    REFERENCES `BRANCHES` (`BRANCH_ID`));

CREATE  TABLE `TOPIC_TYPES`(
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `UUID` VARCHAR(255) NOT NULL ,
  `TITLE` VARCHAR(255) NOT NULL ,
  `DESCRIPTION` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`ID`) ,
  UNIQUE INDEX `UUID` (`UUID` ASC) ,
  UNIQUE INDEX `TITLE` (`TITLE` ASC));