DROP TABLE IF EXISTS workflow_task_limit_config;

/*==============================================================*/
/* Table structure for table tf_directory_cf					*/
/*==============================================================*/

CREATE TABLE workflow_task_limit_config(
  id_task INT DEFAULT NULL,
  number INT DEFAULT NULL,
  PRIMARY KEY  (id_task)
  );
