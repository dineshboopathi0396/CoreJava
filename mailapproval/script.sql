CREATE TABLE WS_MAILAPPROVAL_HISTORY 
(
SNo INT IDENTITY,
PROCESS NVARCHAR(10),
PROCESSINSTID NVARCHAR(20),
RANDOMID NVARCHAR(20),
USERNAME NVARCHAR(50),
WORKSTEP NVARCHAR(50),
COMPLETIONDATE DATETIME,
ACTION NVARCHAR(50),
MODE NVARCHAR(1),
STATUS NVARCHAR(100))