CREATE TABLE T_SINL_USER (
  cod_user            NUMBER(20)                        NOT NULL,
  cod_created_by      NUMBER(20),
  cod_updated_by      NUMBER(20),
  nom_first           VARCHAR2(120)                     NOT NULL,
  nom_last            VARCHAR2(120),
  dat_birth           TIMESTAMP,
  num_phone           VARCHAR2(20),
  num_cpf             VARCHAR2(11),
  txt_email           VARCHAR2(255)                     NOT NULL,
  txt_password_hash   VARCHAR2(255)                     NOT NULL,
  sta_user            VARCHAR2(30),
  sig_role            VARCHAR2(30)                      NOT NULL,
  ind_active          CHAR(1) DEFAULT 'Y'               NOT NULL,
  ind_deleted         CHAR(1) DEFAULT 'N'               NOT NULL,
  ind_email_verified  CHAR(1) DEFAULT 'N'               NOT NULL,
  dat_created         TIMESTAMP DEFAULT SYSTIMESTAMP    NOT NULL,
  dat_updated         TIMESTAMP,
  dat_last_login      TIMESTAMP,
  txt_bio             CLOB,
  val_avatar_url      VARCHAR2(500),
  sig_locale          VARCHAR2(10),
  obs_profile         VARCHAR2(500)
);

CREATE TABLE T_SINL_CATEGORY (
  cod_category        NUMBER(20)                        NOT NULL,
  cod_parent_category NUMBER(20),
  cod_created_by      NUMBER(20),
  cod_updated_by      NUMBER(20),
  nom_category        VARCHAR2(120)                     NOT NULL,
  sig_category        VARCHAR2(10),
  des_category        VARCHAR2(255),
  ind_active          CHAR(1) DEFAULT 'Y'               NOT NULL,
  ind_deleted         CHAR(1) DEFAULT 'N'               NOT NULL,
  dat_created         TIMESTAMP DEFAULT SYSTIMESTAMP    NOT NULL,
  dat_updated         TIMESTAMP,
  obs_category        VARCHAR2(500)
);

CREATE TABLE T_SINL_COURSE (
  cod_course            NUMBER(20)                      NOT NULL,
  cod_user              NUMBER(20),
  cod_category          NUMBER(20),
  cod_created_by        NUMBER(20),
  cod_updated_by        NUMBER(20),
  nom_course            VARCHAR2(120)                   NOT NULL,
  des_course            VARCHAR2(255),
  txt_course            CLOB,
  val_price             NUMBER(10,2) DEFAULT 0,
  sta_course            VARCHAR2(30) DEFAULT 'DRAFT',
  ind_paid              CHAR(1) DEFAULT 'N'             NOT NULL,
  val_thumbnail_url     VARCHAR2(500),
  num_duration_minutes  NUMBER(10),
  num_lessons           NUMBER(10),
  num_students          NUMBER(10),
  num_rating_total      NUMBER(10),
  num_rating_count      NUMBER(10),
  dat_published         TIMESTAMP,
  dat_created           TIMESTAMP DEFAULT SYSTIMESTAMP  NOT NULL,
  dat_updated           TIMESTAMP,
  txt_slug              VARCHAR2(255),
  ind_active            CHAR(1) DEFAULT 'Y'             NOT NULL,
  ind_deleted           CHAR(1) DEFAULT 'N'             NOT NULL,
  obs_course            VARCHAR2(500)
);

CREATE TABLE T_SINL_MODULE (
  cod_module          NUMBER(20)                        NOT NULL,
  cod_course          NUMBER(20)                        NOT NULL,
  cod_created_by      NUMBER(20),
  cod_updated_by      NUMBER(20),
  nom_module          VARCHAR2(120)                     NOT NULL,
  des_module          VARCHAR2(255),
  seq_module          NUMBER(6),
  dat_created         TIMESTAMP DEFAULT SYSTIMESTAMP    NOT NULL,
  dat_updated         TIMESTAMP,
  ind_active          CHAR(1) DEFAULT 'Y'               NOT NULL,
  ind_deleted         CHAR(1) DEFAULT 'N'               NOT NULL,
  obs_module          VARCHAR2(500)
);

CREATE TABLE T_SINL_LESSON (
  cod_lesson                NUMBER(20)                        NOT NULL,
  cod_module                NUMBER(20)                        NOT NULL,
  cod_created_by            NUMBER(20),
  cod_updated_by            NUMBER(20),
  nom_lesson                VARCHAR2(120)                     NOT NULL,
  des_lesson                VARCHAR2(255),
  seq_lesson                NUMBER(6),
  num_duration_seconds      NUMBER(10),
  ind_require_completion    CHAR(1) DEFAULT 'N'               NOT NULL,
  ind_visible               CHAR(1) DEFAULT 'Y'               NOT NULL,
  ind_active                CHAR(1) DEFAULT 'Y'               NOT NULL,
  ind_deleted               CHAR(1) DEFAULT 'N'               NOT NULL,
  dat_created               TIMESTAMP DEFAULT SYSTIMESTAMP    NOT NULL,
  dat_updated               TIMESTAMP,
  val_preview_url           VARCHAR2(500),
  txt_notes                 CLOB,
  obs_lesson                VARCHAR2(500)
);

CREATE TABLE T_SINL_LESSON_CONTENT (
  cod_lesson_content  NUMBER(20)                        NOT NULL,
  cod_lesson          NUMBER(20)                        NOT NULL,
  cod_created_by      NUMBER(20),
  cod_updated_by      NUMBER(20),
  val_video_url       VARCHAR2(500),
  val_transcript_url  VARCHAR2(500),
  val_material_url    VARCHAR2(500),
  txt_content         CLOB,
  ind_has_quiz        CHAR(1) DEFAULT 'N'               NOT NULL,
  ind_active          CHAR(1) DEFAULT 'Y'               NOT NULL,
  ind_deleted         CHAR(1) DEFAULT 'N'               NOT NULL,
  dat_created         TIMESTAMP DEFAULT SYSTIMESTAMP    NOT NULL,
  dat_updated         TIMESTAMP,
  ind_main_content    CHAR(1) DEFAULT 'N'               NOT NULL,
  obs_content         VARCHAR2(500)
);

CREATE TABLE T_SINL_QUIZ (
  cod_quiz           NUMBER(20)                         NOT NULL,
  cod_module         NUMBER(20)                         NOT NULL,
  cod_created_by     NUMBER(20),
  cod_updated_by     NUMBER(20),
  nom_quiz           VARCHAR2(120)                      NOT NULL,
  des_quiz           VARCHAR2(255),
  txt_instructions   VARCHAR2(1000),
  qtd_questions      NUMBER(5),
  val_min_score      NUMBER(5,2),
  qtd_max_attempts   NUMBER(3),
  ind_random_order   CHAR(1) DEFAULT 'N'                NOT NULL,
  ind_active         CHAR(1) DEFAULT 'Y'                NOT NULL,
  ind_deleted        CHAR(1) DEFAULT 'N'                NOT NULL,
  dat_created        TIMESTAMP DEFAULT SYSTIMESTAMP     NOT NULL,
  dat_updated        TIMESTAMP,
  obs_quiz           VARCHAR2(500)
);

CREATE TABLE T_SINL_QUIZ_QUESTION (
  cod_quiz_question   NUMBER(20)                        NOT NULL,
  cod_quiz            NUMBER(20)                        NOT NULL,
  cod_created_by      NUMBER(20),
  cod_updated_by      NUMBER(20),
  seq_question        NUMBER(6),
  txt_question        CLOB                              NOT NULL,
  val_points          NUMBER(10,2)  DEFAULT 1,
  ind_active          CHAR(1) DEFAULT 'Y'               NOT NULL,
  ind_deleted         CHAR(1) DEFAULT 'N'               NOT NULL,
  dat_created         TIMESTAMP DEFAULT SYSTIMESTAMP    NOT NULL,
  dat_updated         TIMESTAMP,
  obs_question        VARCHAR2(500)
);

CREATE TABLE T_SINL_QUIZ_ANSWER_OPTION (
  cod_quiz_answer_option NUMBER(20)                         NOT NULL,
  cod_quiz_question      NUMBER(20)                         NOT NULL,
  cod_created_by         NUMBER(20),
  cod_updated_by         NUMBER(20),
  seq_option             NUMBER(6),
  txt_option             CLOB                               NOT NULL,
  ind_correct            CHAR(1) DEFAULT 'N'                NOT NULL,
  ind_active             CHAR(1) DEFAULT 'Y'                NOT NULL,
  ind_deleted            CHAR(1) DEFAULT 'N'                NOT NULL,
  dat_created            TIMESTAMP DEFAULT SYSTIMESTAMP     NOT NULL,
  dat_updated            TIMESTAMP,
  obs_option             VARCHAR2(500)
);

CREATE TABLE T_SINL_USER_QUIZ_ANSWER (
  cod_user_quiz_answer NUMBER(20)                       NOT NULL,
  cod_user             NUMBER(20)                       NOT NULL,
  cod_quiz_question    NUMBER(20)                       NOT NULL,
  cod_quiz_answer_option NUMBER(20),
  cod_created_by       NUMBER(20),
  cod_updated_by       NUMBER(20),
  dat_answered         TIMESTAMP DEFAULT SYSTIMESTAMP   NOT NULL,
  ind_correct          CHAR(1) DEFAULT 'N'              NOT NULL,
  ind_active           CHAR(1) DEFAULT 'Y'              NOT NULL,
  ind_deleted          CHAR(1) DEFAULT 'N'              NOT NULL,
  dat_created          TIMESTAMP DEFAULT SYSTIMESTAMP   NOT NULL,
  dat_updated          TIMESTAMP,
  obs_answer           VARCHAR2(500)
);

CREATE TABLE T_SINL_USER_QUIZ_ATTEMPT (
  cod_user_quiz_attempt   NUMBER(20)                        NOT NULL,
  cod_user                NUMBER(20)                        NOT NULL,
  cod_quiz                NUMBER(20)                        NOT NULL,
  cod_created_by          NUMBER(20),
  cod_updated_by          NUMBER(20),
  seq_attempt             NUMBER(5)                         NOT NULL,
  val_score               NUMBER(5,2),
  val_max_score           NUMBER(5,2),
  sta_attempt             VARCHAR2(20)                      NOT NULL,
  ind_passed              CHAR(1) DEFAULT 'N'               NOT NULL,
  ind_active              CHAR(1) DEFAULT 'Y'               NOT NULL,
  ind_deleted             CHAR(1) DEFAULT 'N'               NOT NULL,
  dat_started             TIMESTAMP                         NOT NULL,
  dat_finished            TIMESTAMP,
  dat_created             TIMESTAMP DEFAULT SYSTIMESTAMP    NOT NULL,
  dat_updated             TIMESTAMP,
  obs_attempt             VARCHAR2(500)
);

CREATE TABLE T_SINL_USER_COURSE (
  cod_user_course     NUMBER(20)                        NOT NULL,
  cod_user            NUMBER(20)                        NOT NULL,
  cod_course          NUMBER(20)                        NOT NULL,
  cod_created_by      NUMBER(20),
  cod_updated_by      NUMBER(20),
  dat_enrolled        TIMESTAMP DEFAULT SYSTIMESTAMP    NOT NULL,
  dat_completed       TIMESTAMP,
  sta_enrollment      VARCHAR2(30) DEFAULT 'ENROLLED',
  num_progress_percent NUMBER(5) DEFAULT 0,
  val_paid_amount     NUMBER(10,2),
  sig_payment_method  VARCHAR2(20),
  ind_certificate_issued CHAR(1) DEFAULT 'N'            NOT NULL,
  ind_active          CHAR(1) DEFAULT 'Y'               NOT NULL,
  ind_deleted         CHAR(1) DEFAULT 'N'               NOT NULL,
  dat_created         TIMESTAMP DEFAULT SYSTIMESTAMP    NOT NULL,
  dat_updated         TIMESTAMP,
  obs_enrollment      VARCHAR2(500)
);

CREATE TABLE T_SINL_USER_LESSON_PROGRESS (
  cod_user_lesson_progress NUMBER(20)                       NOT NULL,
  cod_user                 NUMBER(20)                       NOT NULL,
  cod_lesson               NUMBER(20)                       NOT NULL,
  cod_created_by           NUMBER(20),
  cod_updated_by           NUMBER(20),
  ind_completed            CHAR(1) DEFAULT 'N'              NOT NULL,
  ind_active               CHAR(1) DEFAULT 'Y'              NOT NULL,
  ind_deleted              CHAR(1) DEFAULT 'N'              NOT NULL,
  dat_first_view           TIMESTAMP,
  dat_last_view            TIMESTAMP,
  dat_created              TIMESTAMP DEFAULT SYSTIMESTAMP   NOT NULL,
  dat_updated              TIMESTAMP,
  dat_completed            TIMESTAMP,
  num_progress_percent     NUMBER(5) DEFAULT 0,
  qtd_views                NUMBER(10) DEFAULT 0,
  obs_progress             VARCHAR2(500)
);

CREATE TABLE T_SINL_REVIEW (
  cod_review          NUMBER(20)                        NOT NULL,
  cod_course          NUMBER(20)                        NOT NULL,
  cod_user            NUMBER(20),
  cod_created_by      NUMBER(20),
  cod_updated_by      NUMBER(20),
  num_rating          NUMBER(3,1),
  txt_comment         CLOB,
  sta_review          VARCHAR2(30) DEFAULT 'PENDING',
  dat_review          TIMESTAMP DEFAULT SYSTIMESTAMP,
  dat_created         TIMESTAMP DEFAULT SYSTIMESTAMP    NOT NULL,
  dat_updated         TIMESTAMP,
  dat_published       TIMESTAMP,
  ind_anonymous       CHAR(1) DEFAULT 'N'               NOT NULL,
  ind_active          CHAR(1) DEFAULT 'Y'               NOT NULL,
  ind_deleted         CHAR(1) DEFAULT 'N'               NOT NULL,
  obs_review          VARCHAR2(500)
);

CREATE TABLE T_SINL_REFRESH_TOKENS (
  cod_refresh_token   NUMBER(20)                        NOT NULL,
  cod_user            NUMBER(20)                        NOT NULL,
  cod_created_by      NUMBER(20),
  cod_updated_by      NUMBER(20),
  txt_token_hash      VARCHAR2(200)                     NOT NULL,
  dat_expires         TIMESTAMP                         NOT NULL,
  dat_revoked         TIMESTAMP,
  dat_created         TIMESTAMP DEFAULT SYSTIMESTAMP    NOT NULL,
  dat_updated         TIMESTAMP,
  ind_revoked         NUMBER(1) DEFAULT 0               NOT NULL,
  ind_active          CHAR(1) DEFAULT 'Y'               NOT NULL,
  ind_deleted         CHAR(1) DEFAULT 'N'               NOT NULL
);


CREATE OR REPLACE TRIGGER TRG_T_SINL_USER_BI
BEFORE INSERT ON T_SINL_USER
FOR EACH ROW
WHEN (new.cod_user IS NULL)
BEGIN
SELECT SEQ_T_SINL_USER.NEXTVAL INTO :new.cod_user FROM dual;
END;

CREATE OR REPLACE TRIGGER TRG_T_SINL_CATEGORY_BI
BEFORE INSERT ON T_SINL_CATEGORY
FOR EACH ROW
WHEN (new.cod_category IS NULL)
BEGIN
SELECT SEQ_T_SINL_CATEGORY.NEXTVAL INTO :new.cod_category FROM dual;
END;

CREATE OR REPLACE TRIGGER TRG_T_SINL_COURSE_BI
BEFORE INSERT ON T_SINL_COURSE
FOR EACH ROW
WHEN (new.cod_course IS NULL)
BEGIN
SELECT SEQ_T_SINL_COURSE.NEXTVAL INTO :new.cod_course FROM dual;
END;

CREATE OR REPLACE TRIGGER TRG_T_SINL_MODULE_BI
BEFORE INSERT ON T_SINL_MODULE
FOR EACH ROW
WHEN (new.cod_module IS NULL)
BEGIN
SELECT SEQ_T_SINL_MODULE.NEXTVAL INTO :new.cod_module FROM dual;
END;

CREATE OR REPLACE TRIGGER TRG_T_SINL_LESSON_BI
BEFORE INSERT ON T_SINL_LESSON
FOR EACH ROW
WHEN (new.cod_lesson IS NULL)
BEGIN
SELECT SEQ_T_SINL_LESSON.NEXTVAL INTO :new.cod_lesson FROM dual;
END;

CREATE OR REPLACE TRIGGER TRG_T_SINL_LESSON_CONTENT_BI
BEFORE INSERT ON T_SINL_LESSON_CONTENT
FOR EACH ROW
WHEN (new.cod_lesson_content IS NULL)
BEGIN
SELECT SEQ_T_SINL_LESSON_CONTENT.NEXTVAL INTO :new.cod_lesson_content FROM dual;
END;

CREATE OR REPLACE TRIGGER TRG_T_SINL_QUIZ_BI
BEFORE INSERT ON T_SINL_QUIZ
FOR EACH ROW
WHEN (new.cod_quiz IS NULL)
BEGIN
SELECT SEQ_T_SINL_QUIZ.NEXTVAL
INTO :new.cod_quiz
FROM dual;
END;

CREATE OR REPLACE TRIGGER TRG_T_SINL_QUIZ_QUESTION_BI
BEFORE INSERT ON T_SINL_QUIZ_QUESTION
FOR EACH ROW
WHEN (new.cod_quiz_question IS NULL)
BEGIN
SELECT SEQ_T_SINL_QUIZ_QUESTION.NEXTVAL INTO :new.cod_quiz_question FROM dual;
END;

CREATE OR REPLACE TRIGGER TRG_T_SINL_QUIZ_ANSWER_OPTION_BI
BEFORE INSERT ON T_SINL_QUIZ_ANSWER_OPTION
FOR EACH ROW
WHEN (new.cod_quiz_answer_option IS NULL)
BEGIN
SELECT SEQ_T_SINL_QUIZ_ANSWER_OPTION.NEXTVAL INTO :new.cod_quiz_answer_option FROM dual;
END;

CREATE OR REPLACE TRIGGER TRG_T_SINL_USER_QUIZ_ANSWER_BI
BEFORE INSERT ON T_SINL_USER_QUIZ_ANSWER
FOR EACH ROW
WHEN (new.cod_user_quiz_answer IS NULL)
BEGIN
SELECT SEQ_T_SINL_USER_QUIZ_ANSWER.NEXTVAL INTO :new.cod_user_quiz_answer FROM dual;
END;

CREATE OR REPLACE TRIGGER TRG_T_SINL_UQA_BI
BEFORE INSERT ON T_SINL_USER_QUIZ_ATTEMPT
FOR EACH ROW
WHEN (new.cod_user_quiz_attempt IS NULL)
BEGIN
SELECT SEQ_T_SINL_USER_QUIZ_ATTEMPT.NEXTVAL
INTO :new.cod_user_quiz_attempt
FROM dual;
END;

CREATE OR REPLACE TRIGGER TRG_T_SINL_USER_COURSE_BI
BEFORE INSERT ON T_SINL_USER_COURSE
FOR EACH ROW
WHEN (new.cod_user_course IS NULL)
BEGIN
SELECT SEQ_T_SINL_USER_COURSE.NEXTVAL INTO :new.cod_user_course FROM dual;
END;

CREATE OR REPLACE TRIGGER TRG_T_SINL_USER_LESSON_PROGRESS_BI
BEFORE INSERT ON T_SINL_USER_LESSON_PROGRESS
FOR EACH ROW
WHEN (new.cod_user_lesson_progress IS NULL)
BEGIN
SELECT SEQ_T_SINL_USER_LESSON_PROGRESS.NEXTVAL INTO :new.cod_user_lesson_progress FROM dual;
END;

CREATE OR REPLACE TRIGGER TRG_T_SINL_REVIEW_BI
BEFORE INSERT ON T_SINL_REVIEW
FOR EACH ROW
WHEN (new.cod_review IS NULL)
BEGIN
SELECT SEQ_T_SINL_REVIEW.NEXTVAL INTO :new.cod_review FROM dual;
END;

CREATE OR REPLACE TRIGGER TRG_T_SINL_REFRESH_TOKENS_BI
BEFORE INSERT ON T_SINL_REFRESH_TOKENS
FOR EACH ROW
WHEN (new.cod_refresh_token IS NULL)
BEGIN
SELECT SEQ_SINL_REFRESH_TOKENS.NEXTVAL INTO :new.cod_refresh_token FROM dual;
END;

