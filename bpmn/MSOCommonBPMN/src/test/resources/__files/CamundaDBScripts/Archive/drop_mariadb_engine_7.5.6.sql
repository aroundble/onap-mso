use camundabpmn;
/* 
Drop a archive tables
*/
/*-- TMP_ARCHIVING_PROCINST */
DROP TABLE IF EXISTS TMP_ARCHIVING_PROCINST;

/*-- TMP_ARCHIVING_BYTEARRAY */
DROP TABLE IF EXISTS TMP_ARCHIVING_BYTEARRAY;

/*-- TMP LOG TABLE */
DROP TABLE IF EXISTS TMPLOGTABLE;

/* -- Camunda Hi Tables --*/
DROP TABLE IF EXISTS Camunda_Hi_Tables;

/* drop own extentions columns:
alter table  ARCHIVE_ACT_HI_PROCINST DROP (STAT_EXECUTION_ID, STAT_EXECUTION_TS);
alter table  ARCHIVE_ACT_HI_ACTINST DROP (STAT_EXECUTION_ID, STAT_EXECUTION_TS);
alter table  ARCHIVE_ACT_HI_TASKINST DROP (STAT_EXECUTION_ID, STAT_EXECUTION_TS);
alter table  ARCHIVE_ACT_HI_VARINST DROP (STAT_EXECUTION_ID, STAT_EXECUTION_TS);
alter table  ARCHIVE_ACT_HI_DETAIL DROP (STAT_EXECUTION_ID, STAT_EXECUTION_TS);
alter table  ARCHIVE_ACT_HI_COMMENT DROP (STAT_EXECUTION_ID, STAT_EXECUTION_TS);
alter table  ARCHIVE_ACT_HI_ATTACHMENT DROP (STAT_EXECUTION_ID, STAT_EXECUTION_TS);
alter table  ARCHIVE_ACT_HI_OP_LOG DROP (STAT_EXECUTION_ID, STAT_EXECUTION_TS);
alter table  ARCHIVE_ACT_HI_INCIDENT DROP (STAT_EXECUTION_ID, STAT_EXECUTION_TS);
*/

/*--#1 */
DROP TABLE IF EXISTS ARCHIVE_ACT_HI_PROCINST;
/*--#2 */
DROP TABLE IF EXISTS ARCHIVE_ACT_HI_ACTINST;
/*--#3 */
DROP TABLE IF EXISTS ARCHIVE_ACT_HI_TASKINST;
/*--#4 */
DROP TABLE IF EXISTS ARCHIVE_ACT_HI_VARINST;
/*--#5 */
DROP TABLE IF EXISTS ARCHIVE_ACT_HI_DETAIL;
/*--#6 */
DROP TABLE IF EXISTS ARCHIVE_ACT_HI_COMMENT;
/*--#7 */
DROP TABLE IF EXISTS ARCHIVE_ACT_HI_ATTACHMENT;
/*--#8 */
DROP TABLE IF EXISTS ARCHIVE_ACT_HI_OP_LOG;
/*--#9 */
DROP TABLE IF EXISTS ARCHIVE_ACT_HI_INCIDENT;
/*--#10 */
DROP TABLE IF EXISTS ARCHIVE_ACT_GE_BYTEARRAY;

/* drop PL SQL procedures: */
DROP PROCEDURE IF EXISTS ARCHIVE_CAMUNDA_HISTORY;
DROP PROCEDURE IF EXISTS ROLLB_ARCHIVE_CAMUNDA_HISTORY;
 
/*-- Sequence */
-- as sequence drop doesn't work automatically in MariaDB, use this procedure to drop sequence
 DROP PROCEDURE IF EXISTS DropSequence;
 
  DELIMITER //
 
  CREATE PROCEDURE DropSequence (vname VARCHAR(30))
  BEGIN
     -- Drop the sequence
     DELETE FROM _sequences WHERE name = vname;  
  END
  //
  DELIMITER ;

-- use the above procedure to drop sequence 
CALL DropSequence('STAT_EXECUTION_SEQ');

/*-- To Drop the MariaDB specific user defined procedures and functions */
DROP FUNCTION IF EXISTS NextVal;
DROP PROCEDURE IF EXISTS CreateSequence;
DROP PROCEDURE IF EXISTS DropSequence;
DROP TABLE IF EXISTS _sequences;