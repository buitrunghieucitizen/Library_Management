SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

IF COL_LENGTH('dbo.Staff', 'Email') IS NULL
BEGIN
    ALTER TABLE dbo.Staff ADD Email NVARCHAR(100) NULL;
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'UQ_Staff_Email'
      AND object_id = OBJECT_ID('dbo.Staff')
)
BEGIN
    CREATE UNIQUE NONCLUSTERED INDEX UQ_Staff_Email
    ON dbo.Staff (Email)
    WHERE Email IS NOT NULL;
END
GO

UPDATE dbo.Staff
SET Email = LOWER(LTRIM(RTRIM(Username)))
WHERE Email IS NULL
  AND Username IS NOT NULL
  AND LTRIM(RTRIM(Username)) LIKE '%_@_%._%';
GO
