SET ANSI_NULLS ON;
GO
SET QUOTED_IDENTIFIER ON;
GO

IF COL_LENGTH('dbo.Student', 'AvatarUrl') IS NULL
BEGIN
    ALTER TABLE dbo.Student ADD AvatarUrl NVARCHAR(500) NULL;
END
GO

IF COL_LENGTH('dbo.Student', 'ClassName') IS NULL
BEGIN
    ALTER TABLE dbo.Student ADD ClassName NVARCHAR(100) NULL;
END
GO

IF COL_LENGTH('dbo.Student', 'FacultyName') IS NULL
BEGIN
    ALTER TABLE dbo.Student ADD FacultyName NVARCHAR(100) NULL;
END
GO

IF COL_LENGTH('dbo.Student', 'AccountStatus') IS NULL
BEGIN
    ALTER TABLE dbo.Student ADD AccountStatus NVARCHAR(30) NULL;
END
GO

IF COL_LENGTH('dbo.Student', 'AccountStatus') IS NOT NULL
BEGIN
    UPDATE dbo.Student
    SET AccountStatus = N'Active'
    WHERE AccountStatus IS NULL
        OR LTRIM(RTRIM(AccountStatus)) = N'';
END
GO

IF COL_LENGTH('dbo.Student', 'AccountStatus') IS NOT NULL
    AND NOT EXISTS (
        SELECT 1
        FROM sys.default_constraints dc
        JOIN sys.columns c
            ON c.default_object_id = dc.object_id
        JOIN sys.tables t
            ON t.object_id = c.object_id
        WHERE t.name = 'Student' AND c.name = 'AccountStatus'
    )
BEGIN
    ALTER TABLE dbo.Student
    ADD CONSTRAINT DF_Student_AccountStatus
    DEFAULT N'Active' FOR AccountStatus;
END
GO

IF COL_LENGTH('dbo.Student', 'AccountStatus') IS NOT NULL
BEGIN
    ALTER TABLE dbo.Student ALTER COLUMN AccountStatus NVARCHAR(30) NOT NULL;
END
GO

IF COL_LENGTH('dbo.Student', 'CreatedAt') IS NULL
BEGIN
    ALTER TABLE dbo.Student ADD CreatedAt DATETIME NULL;
END
GO

IF COL_LENGTH('dbo.Student', 'CreatedAt') IS NOT NULL
BEGIN
    UPDATE dbo.Student
    SET CreatedAt = GETDATE()
    WHERE CreatedAt IS NULL;
END
GO

IF COL_LENGTH('dbo.Student', 'CreatedAt') IS NOT NULL
    AND NOT EXISTS (
        SELECT 1
        FROM sys.default_constraints dc
        JOIN sys.columns c
            ON c.default_object_id = dc.object_id
        JOIN sys.tables t
            ON t.object_id = c.object_id
        WHERE t.name = 'Student' AND c.name = 'CreatedAt'
    )
BEGIN
    ALTER TABLE dbo.Student
    ADD CONSTRAINT DF_Student_CreatedAt
    DEFAULT GETDATE() FOR CreatedAt;
END
GO

IF COL_LENGTH('dbo.Student', 'CreatedAt') IS NOT NULL
BEGIN
    ALTER TABLE dbo.Student ALTER COLUMN CreatedAt DATETIME NOT NULL;
END
GO

IF COL_LENGTH('dbo.Book', 'Description') IS NULL
BEGIN
    ALTER TABLE dbo.Book ADD Description NVARCHAR(1000) NULL;
END
GO

IF COL_LENGTH('dbo.Book', 'ShelfLocation') IS NULL
BEGIN
    ALTER TABLE dbo.Book ADD ShelfLocation NVARCHAR(100) NULL;
END
GO

IF COL_LENGTH('dbo.Book', 'ImageUrl') IS NULL
BEGIN
    ALTER TABLE dbo.Book ADD ImageUrl NVARCHAR(500) NULL;
END
GO
