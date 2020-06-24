CREATE TABLE "ISSUES_COPY"(
    "KEE" VARCHAR(50) NOT NULL,
    "RULE_ID" INTEGER,
    "SEVERITY" VARCHAR(10),
    "MANUAL_SEVERITY" BOOLEAN NOT NULL,
    "MESSAGE" VARCHAR(4000),
    "LINE" INTEGER,
    "GAP" DOUBLE,
    "STATUS" VARCHAR(20),
    "RESOLUTION" VARCHAR(20),
    "CHECKSUM" VARCHAR(1000),
    "REPORTER" VARCHAR(255),
    "ASSIGNEE" VARCHAR(255),
    "AUTHOR_LOGIN" VARCHAR(255),
    "ACTION_PLAN_KEY" VARCHAR(50),
    "ISSUE_ATTRIBUTES" VARCHAR(4000),
    "EFFORT" INTEGER,
    "CREATED_AT" BIGINT,
    "UPDATED_AT" BIGINT,
    "ISSUE_CREATION_DATE" BIGINT,
    "ISSUE_UPDATE_DATE" BIGINT,
    "ISSUE_CLOSE_DATE" BIGINT,
    "TAGS" VARCHAR(4000),
    "COMPONENT_UUID" VARCHAR(50),
    "PROJECT_UUID" VARCHAR(50),
    "LOCATIONS" BLOB,
    "ISSUE_TYPE" TINYINT,
    "FROM_HOTSPOT" BOOLEAN,
    "RULE_UUID" VARCHAR(40)
);