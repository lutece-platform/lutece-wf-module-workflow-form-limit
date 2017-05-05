DROP TABLE IF EXISTS workflow_task_formlimit_config;

/*==============================================================*/
/* Table structure for table tf_directory_cf					*/
/*==============================================================*/

CREATE TABLE workflow_task_formlimit_config(
  id_task INT NOT NULL,
  number INT DEFAULT NULL,
  PRIMARY KEY  (id_task)
  );
